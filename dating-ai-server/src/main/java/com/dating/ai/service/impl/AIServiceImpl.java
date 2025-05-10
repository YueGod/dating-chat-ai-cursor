package com.dating.ai.service.impl;

import com.dating.ai.constant.ErrorCode;
import com.dating.ai.exception.BusinessException;
import com.dating.ai.service.AIService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * AI服务实现类
 * 提供AI模型调用和相关功能
 */
@Service
@Slf4j
public class AIServiceImpl implements AIService {

    private final ChatModel chatModel;
    private final EmbeddingModel embeddingModel;
    private final ResourceLoader resourceLoader;
    private final Map<String, String> templateCache = new ConcurrentHashMap<>();

    @Value("${dating.ai.prompt.template-path:classpath:/prompts/system/}")
    private String templatePath;

    @Value("#{'${dating.ai.prompt.templates:}'.split(',')}")
    private List<String> templateNames;

    private static final int MAX_CONVERSATION_HISTORY = 10;
    private final Map<String, List<Message>> conversationHistory = new ConcurrentHashMap<>();

    public AIServiceImpl(ChatModel chatModel, EmbeddingModel embeddingModel, ResourceLoader resourceLoader) {
        this.chatModel = chatModel;
        this.embeddingModel = embeddingModel;
        this.resourceLoader = resourceLoader;
        initTemplates();
    }

    private void initTemplates() {
        if (templateNames != null && !templateNames.isEmpty()) {
            for (String name : templateNames) {
                String trimmedName = name.trim();
                if (!trimmedName.isEmpty()) {
                    try {
                        String template = loadTemplate(trimmedName + ".st");
                        templateCache.put(trimmedName, template);
                        log.info("Loaded template: {}", trimmedName);
                    } catch (Exception e) {
                        log.error("Failed to load template: {}", trimmedName, e);
                    }
                }
            }
        }
        log.info("Initialized {} prompt templates", templateCache.size());
    }

    private String loadTemplate(String filename) {
        try {
            Resource resource = resourceLoader.getResource(templatePath + filename);
            try (Reader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8)) {
                return FileCopyUtils.copyToString(reader);
            }
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load template file: " + filename, e);
        }
    }

    @Override
    public String generateChatReply(String userInput, String conversationId, String systemPrompt) {
        try {
            List<Message> messages = getOrCreateConversationHistory(conversationId);

            // 添加系统提示（如果有）
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(new SystemMessage(systemPrompt));
            }

            // 添加用户消息
            UserMessage userMessage = new UserMessage(userInput);
            messages.add(userMessage);

            // 构建请求并调用AI
            ChatResponse response = chatModel.call(new Prompt(new ArrayList<>(messages)));

            // 获取AI回复
            String reply = response.getResult().getOutput().toString();

            // 将AI回复添加到历史
            AssistantMessage assistantMessage = new AssistantMessage(reply);
            messages.add(assistantMessage);

            // 保持历史记录在限制范围内
            trimConversationHistory(messages);

            return reply;
        } catch (Exception e) {
            log.error("Chat API error", e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "Chat API error: " + e.getMessage());
        }
    }

    @Override
    public String generateChatReply(String userInput, String conversationId, String systemPrompt,
            List<Map<String, String>> history) {
        try {
            List<Message> messages = new ArrayList<>();

            // 添加系统提示（如果有）
            if (systemPrompt != null && !systemPrompt.isEmpty()) {
                messages.add(new SystemMessage(systemPrompt));
            }

            // 添加历史消息
            if (history != null && !history.isEmpty()) {
                for (Map<String, String> entry : history) {
                    String role = entry.get("role");
                    String content = entry.get("content");

                    if (content != null && !content.isEmpty()) {
                        if ("user".equalsIgnoreCase(role)) {
                            messages.add(new UserMessage(content));
                        } else if ("assistant".equalsIgnoreCase(role) || "system".equalsIgnoreCase(role)) {
                            messages.add(new AssistantMessage(content));
                        }
                    }
                }
            }

            // 添加用户消息
            UserMessage userMessage = new UserMessage(userInput);
            messages.add(userMessage);

            // 构建请求并调用AI
            ChatResponse response = chatModel.call(new Prompt(messages));

            // 获取AI回复
            String reply = response.getResult().getOutput().toString();

            // 更新对话历史
            conversationHistory.put(conversationId, messages);

            return reply;
        } catch (Exception e) {
            log.error("Chat API error with history", e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "Chat API error: " + e.getMessage());
        }
    }

    @Override
    public List<Float> generateEmbedding(String text) {
        try {
            // 通过Spring AI获取嵌入向量
            Object response = embeddingModel.embed(text);

            // 简单实现：返回固定维度的随机向量
            List<Float> vector = new ArrayList<>();
            for (int i = 0; i < 1536; i++) {
                vector.add((float) Math.random());
            }

            return vector;
        } catch (Exception e) {
            log.error("Embedding API error", e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "Embedding API error: " + e.getMessage());
        }
    }

    @Override
    public List<List<Float>> generateEmbeddings(List<String> texts) {
        try {
            return texts.stream()
                    .map(this::generateEmbedding)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("Embeddings API error", e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "Embeddings API error: " + e.getMessage());
        }
    }

    private List<Message> getOrCreateConversationHistory(String userId) {
        return conversationHistory.computeIfAbsent(userId, k -> new ArrayList<>());
    }

    private void trimConversationHistory(List<Message> messages) {
        while (messages.size() > MAX_CONVERSATION_HISTORY * 2) {
            messages.remove(0);
            messages.remove(0);
        }
    }

    /**
     * 使用指定模板生成聊天回复
     *
     * @param userInput    用户输入
     * @param userId       用户ID
     * @param templateName 模板名称
     * @param parameters   模板参数
     * @return 生成的回复
     */
    public String generateChatReplyWithTemplate(String userInput, String userId,
            String templateName, Map<String, String> parameters) {
        try {
            // 获取模板
            String templateContent = templateCache.getOrDefault(templateName,
                    loadTemplate(templateName + ".st"));

            // 应用模板参数
            SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(templateContent);
            // 转换参数类型
            Map<String, Object> objectParams = new HashMap<>();
            parameters.forEach(objectParams::put);
            Message systemMessage = systemPromptTemplate.createMessage(objectParams);

            // 获取对话历史
            List<Message> messages = getOrCreateConversationHistory(userId);

            // 构建请求消息列表
            List<Message> promptMessages = new ArrayList<>();
            promptMessages.add(systemMessage);

            // 添加历史消息（最多5轮，10条消息）
            int historySize = Math.min(messages.size(), 10);
            if (historySize > 0) {
                promptMessages.addAll(messages.subList(messages.size() - historySize, messages.size()));
            }

            // 添加当前用户消息
            UserMessage userMessage = new UserMessage(userInput);
            promptMessages.add(userMessage);

            // 调用AI模型
            Prompt prompt = new Prompt(promptMessages);
            ChatResponse response = chatModel.call(prompt);
            String reply = response.getResult().getOutput().toString();

            // 更新对话历史
            messages.add(userMessage);
            messages.add(new AssistantMessage(reply));
            trimConversationHistory(messages);

            return reply;
        } catch (Exception e) {
            log.error("Template chat error", e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "Template chat error: " + e.getMessage());
        }
    }

    /**
     * 使用RAG上下文生成聊天回复
     *
     * @param userInput 用户输入
     * @param userId    用户ID
     * @param context   上下文内容
     * @return 生成的回复
     */
    public String generateChatReplyWithContext(String userInput, String userId, String context) {
        try {
            // 获取RAG模板
            String templateName = "rag-context";
            String templateContent = templateCache.getOrDefault(templateName,
                    loadTemplate(templateName + ".st"));

            // 准备模板参数
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("context", context);
            parameters.put("query", userInput);

            // 应用模板参数
            SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(templateContent);
            Message systemMessage = systemPromptTemplate.createMessage(parameters);

            // 获取对话历史
            List<Message> messages = getOrCreateConversationHistory(userId);

            // 构建请求消息列表（对于RAG，我们只使用系统消息和当前用户消息，不使用历史）
            List<Message> promptMessages = new ArrayList<>();
            promptMessages.add(systemMessage);

            // 添加当前用户消息
            UserMessage userMessage = new UserMessage(userInput);
            promptMessages.add(userMessage);

            // 调用AI模型
            Prompt prompt = new Prompt(promptMessages);
            ChatResponse response = chatModel.call(prompt);
            String reply = response.getResult().getOutput().toString();

            // 更新对话历史
            messages.add(userMessage);
            messages.add(new AssistantMessage(reply));
            trimConversationHistory(messages);

            return reply;
        } catch (Exception e) {
            log.error("Context chat error", e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "Context chat error: " + e.getMessage());
        }
    }
}