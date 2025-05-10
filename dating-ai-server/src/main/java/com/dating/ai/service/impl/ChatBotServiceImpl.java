package com.dating.ai.service.impl;

import com.dating.ai.constant.ErrorCode;
import com.dating.ai.domain.ChatMessage;
import com.dating.ai.domain.Conversation;
import com.dating.ai.dto.ChatRequestDTO;
import com.dating.ai.dto.ChatResponseDTO;
import com.dating.ai.exception.BusinessException;
import com.dating.ai.service.AIService;
import com.dating.ai.service.ChatBotService;
import com.dating.ai.service.MembershipService;
import com.dating.ai.service.VectorService;
import com.dating.ai.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 聊天机器人服务实现类
 * 整合AI服务和向量服务，提供智能聊天功能
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ChatBotServiceImpl implements ChatBotService {

    private final AIService aiService;
    private final VectorService vectorService;
    private final MembershipService membershipService;

    /**
     * Qdrant消息集合名称
     */
    @Value("${qdrant.collections.messages:dating_messages}")
    private String messagesCollection;

    /**
     * 系统提示词模板路径
     */
    @Value("${ai.conversation.system-prompts-path:classpath:prompts/system}")
    private String systemPromptsPath;

    /**
     * 初始化存储，确保必要的数据结构已创建
     */
    public void initialize() {
        try {
            // 检查并创建向量集合
            ensureCollectionExists(messagesCollection, "COSINE");
            log.info("聊天机器人服务初始化完成");
        } catch (Exception e) {
            log.error("聊天机器人服务初始化失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "初始化聊天机器人服务失败");
        }
    }

    /**
     * 处理聊天请求，生成AI回复
     *
     * @param chatRequest 聊天请求
     * @return 聊天响应
     */
    @Override
    @Transactional
    public ChatResponseDTO chat(ChatRequestDTO chatRequest) {
        String userId = UserContext.getUserId();
        String conversationId = chatRequest.getConversationId();
        String styleId = chatRequest.getStyleId();
        String message = chatRequest.getMessage();

        log.info("接收到聊天请求 - 用户: {}, 会话: {}, 风格: {}", userId, conversationId, styleId);

        // 检查会员权限
        if (styleId != null && !styleId.isEmpty() && !membershipService.checkVipAccess(styleId)) {
            throw new BusinessException(ErrorCode.Membership.VIP_ACCESS_DENIED, "该聊天风格需要VIP会员");
        }

        try {
            // 获取系统提示词
            String systemPrompt = getSystemPrompt(styleId);

            // 生成AI回复
            String aiReply = aiService.generateChatReply(message, conversationId, systemPrompt);

            // 存储消息向量（异步处理）
            storeMessageVectorAsync(conversationId, message, "user");
            storeMessageVectorAsync(conversationId, aiReply, "assistant");

            // 构建响应
            ChatResponseDTO response = new ChatResponseDTO();
            response.setConversationId(conversationId);
            response.setMessage(aiReply);
            response.setTimestamp(new Date());

            log.info("聊天响应已生成 - 用户: {}, 会话: {}", userId, conversationId);
            return response;
        } catch (Exception e) {
            log.error("处理聊天请求失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.Chat.MESSAGE_SEND_FAILED, "聊天消息处理失败: " + e.getMessage());
        }
    }

    /**
     * 检查并创建向量集合
     *
     * @param collectionName 集合名称
     * @param distance       距离类型
     */
    private void ensureCollectionExists(String collectionName, String distance) {
        try {
            // 尝试删除已存在的集合（仅用于开发和测试）
            // vectorService.deleteCollection(collectionName);

            // 创建向量集合
            vectorService.createCollection(collectionName, distance);
            log.info("成功创建向量集合: {}", collectionName);
        } catch (Exception e) {
            // 忽略已存在的集合错误
            log.info("向量集合可能已存在: {}, {}", collectionName, e.getMessage());
        }
    }

    /**
     * 异步存储消息向量
     *
     * @param conversationId 会话ID
     * @param message        消息内容
     * @param role           消息角色 (user/assistant)
     */
    private void storeMessageVectorAsync(String conversationId, String message, String role) {
        // 在生产环境中应使用异步处理
        new Thread(() -> {
            try {
                // 生成嵌入向量
                List<Float> vector = aiService.generateEmbedding(message);

                // 准备元数据
                Map<String, Object> metadata = new HashMap<>();
                metadata.put("conversationId", conversationId);
                metadata.put("text", message);
                metadata.put("role", role);
                metadata.put("timestamp", new Date().getTime());

                // 存储向量
                vectorService.upsertVector(messagesCollection, vector, metadata);
                log.debug("成功存储消息向量 - 会话: {}, 角色: {}", conversationId, role);
            } catch (Exception e) {
                log.error("存储消息向量失败: {}", e.getMessage(), e);
            }
        }).start();
    }

    /**
     * 获取系统提示词
     *
     * @param styleId 风格ID
     * @return 系统提示词
     */
    private String getSystemPrompt(String styleId) {
        if (styleId == null || styleId.isEmpty()) {
            // 使用默认提示词
            return loadDefaultSystemPrompt();
        }

        // TODO: 从数据库加载特定风格的提示词
        return loadDefaultSystemPrompt();
    }

    /**
     * 加载默认系统提示词
     *
     * @return 默认系统提示词
     */
    private String loadDefaultSystemPrompt() {
        try {
            // 在开发环境从文件加载，在生产环境可以从数据库或配置中心加载
            Path path = Paths.get(systemPromptsPath, "default.txt");
            if (Files.exists(path)) {
                return Files.readString(path);
            }

            // 如果文件不存在，返回硬编码的默认提示词
            return "你是一个有魅力的AI约会助手，能够自然地交流和回应用户的消息。" +
                    "提供有深度、有趣且富有人情味的回复，展现温暖、善解人意和风趣的性格。" +
                    "你的回复应该自然、流畅，像真人一样，避免过于机械或死板的回答。";
        } catch (IOException e) {
            log.error("加载默认系统提示词失败: {}", e.getMessage(), e);
            return "你是一个AI约会助手，回答用户的问题。";
        }
    }

    /**
     * 基于语义搜索相关消息
     *
     * @param query          查询文本
     * @param conversationId 会话ID
     * @param limit          返回结果数量限制
     * @return 相关消息列表
     */
    @Override
    public List<Map<String, Object>> searchRelatedMessages(String query, String conversationId, int limit) {
        try {
            // 生成查询文本的嵌入向量
            List<Float> queryVector = aiService.generateEmbedding(query);

            // 在向量数据库中搜索相似向量
            List<Map<String, Object>> results = vectorService.searchVectors(
                    messagesCollection,
                    queryVector,
                    limit,
                    0.7f // 相似度阈值
            );

            // 如果指定了会话ID，过滤只属于该会话的结果
            if (conversationId != null && !conversationId.isEmpty()) {
                results = results.stream()
                        .filter(r -> conversationId.equals(r.get("conversationId")))
                        .collect(Collectors.toList());
            }

            return results;
        } catch (Exception e) {
            log.error("搜索相关消息失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "搜索相关消息失败");
        }
    }
}