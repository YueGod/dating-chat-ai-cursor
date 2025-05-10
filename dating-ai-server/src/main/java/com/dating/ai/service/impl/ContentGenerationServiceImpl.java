package com.dating.ai.service.impl;

import com.dating.ai.dto.ConversationContext;
import com.dating.ai.dto.EmotionAnalysisResult;
import com.dating.ai.dto.ResponseStrategy;
import com.dating.ai.dto.ResponseStrategy.PersonalizationElement;
import com.dating.ai.service.AIService;
import com.dating.ai.service.ContentGenerationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 内容生成服务实现类
 * 负责根据情绪分析和策略生成AI回复内容
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentGenerationServiceImpl implements ContentGenerationService {

    private final AIService aiService;

    /**
     * 提示词模板路径
     */
    @Value("${dating.ai.prompt.template-path:classpath:/prompts/system/}")
    private String templatePath;

    /**
     * 每种策略的最大回复长度
     */
    @Value("#{${dating.ai.chat.ai.max-response-length-by-strategy:{'HIGH_ENGAGEMENT':150,'MEDIUM_HIGH_ENGAGEMENT':120,'MEDIUM_ENGAGEMENT':100,'LOW_ENGAGEMENT':60,'VERY_LOW_ENGAGEMENT':40}}}")
    private java.util.Map<String, Integer> maxResponseLengthByStrategy;

    @Override
    public String generateResponse(String userInput, EmotionAnalysisResult emotion,
            ResponseStrategy strategy, ConversationContext context) {
        try {
            // 构建提示词
            String prompt = buildResponseGenerationPrompt(userInput, emotion, strategy, context);

            // 生成系统提示词
            String systemPrompt = loadSystemPrompt("consolidated-response.st");

            // 调用AI服务生成回复
            String aiResponse = aiService.generateChatReply(prompt,
                    context.getUserProfile().getUserId() + "_response", systemPrompt);

            // 后处理回复
            String processedResponse = postProcessResponse(aiResponse, strategy, context);

            log.debug("内容生成成功 - 用户: {}, 情绪分数: {}, 策略类型: {}",
                    context.getUserProfile().getUserId(), emotion.getTotalScore(),
                    strategy.getType());

            return processedResponse;
        } catch (Exception e) {
            log.error("内容生成失败: {}", e.getMessage(), e);

            // 返回备用回复
            return getFallbackResponse(strategy.getType());
        }
    }

    @Override
    public String buildResponseGenerationPrompt(String userInput, EmotionAnalysisResult emotion,
            ResponseStrategy strategy, ConversationContext context) {
        StringBuilder prompt = new StringBuilder();

        // 基础指令
        prompt.append("根据以下情绪分析结果和对话历史，生成自然、个性化的回复：\n\n");

        // 情绪分析结果
        prompt.append("情绪分析:\n");
        prompt.append("- 总分: ").append(emotion.getTotalScore()).append("/100\n");
        prompt.append("- 情绪状态: ").append(emotion.getEmotionalState()).append("\n");
        prompt.append("- 互动意愿: ").append(emotion.getInteractionLevel()).append("\n");
        prompt.append("- 主要意图: ").append(emotion.getPrimaryIntent()).append("\n");
        if (emotion.getTopics() != null && emotion.getTopics().length > 0) {
            prompt.append("- 主题关键词: ").append(String.join(", ", emotion.getTopics())).append("\n");
        }
        prompt.append("\n");

        // 对话历史
        if (context != null && context.getRecentMessages() != null && !context.getRecentMessages().isEmpty()) {
            prompt.append("对话历史:\n");
            List<ConversationContext.MessagePair> recentMessages = context.getRecentMessages();
            // 最多包含5轮历史消息
            int historySize = Math.min(recentMessages.size(), 5);
            for (int i = 0; i < historySize; i++) {
                ConversationContext.MessagePair message = recentMessages.get(i);
                prompt.append("用户: \"").append(message.getUserMessage()).append("\"\n");
                if (message.getAiResponse() != null) {
                    prompt.append("助手: \"").append(message.getAiResponse()).append("\"\n");
                }
            }
            prompt.append("\n");
        }

        // 当前话题信息
        prompt.append("当前话题: ").append(context != null ? context.getCurrentTopic() : "一般交流").append("\n");

        // 话题切换信息
        prompt.append("需要话题切换: ").append(strategy.isNeedTopicSwitch()).append("\n");
        if (strategy.isNeedTopicSwitch()) {
            prompt.append("目标话题: ").append(strategy.getTargetTopic()).append("\n");
        }

        // 回复策略
        prompt.append("回复策略: ").append(strategy.getType()).append("\n\n");

        // 个性化元素
        if (strategy.getPersonalizationElements() != null && !strategy.getPersonalizationElements().isEmpty()) {
            prompt.append("使用以下个性化元素:\n");
            for (PersonalizationElement element : strategy.getPersonalizationElements()) {
                prompt.append("- ").append(element.getContent()).append("\n");
            }
            prompt.append("\n");
        }

        // 当前用户输入
        prompt.append("用户最新输入: \"").append(userInput).append("\"\n\n");

        // 最终指令
        prompt.append("回复需要:\n");
        prompt.append("1. 直接回应用户输入\n");
        prompt.append("2. 表达适当情感\n");
        prompt.append("3. 加入指定的个性化元素\n");
        prompt.append("4. ").append(strategy.isNeedTopicSwitch() ? "自然过渡到新话题" : "继续发展当前话题").append("\n");
        prompt.append("5. 包含互动性问题或陈述\n\n");

        // 风格指引
        prompt.append("保持自然友好的语气，回复应当看起来像一个真实的人，包括自然的语言习惯、思考过程和情感表达。\n");
        prompt.append("避免过度正式或机械化的表达，使用适当的口语化表达和语气词。\n\n");

        // 长度指引
        Integer maxLength = getMaxResponseLength(strategy.getType().toString());
        if (maxLength != null) {
            prompt.append("回复长度控制在").append(maxLength).append("字以内。\n");
        }

        return prompt.toString();
    }

    @Override
    public String postProcessResponse(String rawResponse, ResponseStrategy strategy,
            ConversationContext context) {
        if (rawResponse == null || rawResponse.isEmpty()) {
            return getFallbackResponse(strategy.getType());
        }

        // 移除可能的引号和前缀
        String processed = rawResponse.trim();
        if (processed.startsWith("\"") && processed.endsWith("\"")) {
            processed = processed.substring(1, processed.length() - 1);
        }

        // 如果响应太长，适当缩减
        Integer maxLength = getMaxResponseLength(strategy.getType().toString());
        if (maxLength != null && processed.length() > maxLength) {
            // 尝试在自然断点处截断
            processed = truncateAtNaturalBreakpoint(processed, maxLength);
        }

        // 修复常见文本问题
        processed = fixCommonTextIssues(processed);

        return processed;
    }

    /**
     * 加载系统提示词
     */
    private String loadSystemPrompt(String fileName) {
        try {
            // 尝试从文件系统加载
            Path path = Paths.get(templatePath, fileName);
            if (Files.exists(path)) {
                return Files.readString(path);
            }

            // 如果文件系统加载失败，则使用默认提示词
            return getDefaultSystemPrompt();
        } catch (IOException e) {
            log.error("加载系统提示词失败: {}", e.getMessage(), e);
            return getDefaultSystemPrompt();
        }
    }

    /**
     * 获取默认系统提示词
     */
    private String getDefaultSystemPrompt() {
        return "你是一个有魅力的AI约会助手，能够自然地交流和回应用户的消息。" +
                "提供有深度、有趣且富有人情味的回复，展现温暖、善解人意和风趣的性格。" +
                "你的回复应该自然、流畅，像真人一样，避免过于机械或死板的回答。" +
                "使用适当的填充词和停顿，展示思考过程，偶尔表现出轻微的自我修正。";
    }

    /**
     * 获取特定策略类型的最大回复长度
     */
    private Integer getMaxResponseLength(String strategyType) {
        return maxResponseLengthByStrategy.getOrDefault(strategyType, 100);
    }

    /**
     * 在自然断点处截断文本
     */
    private String truncateAtNaturalBreakpoint(String text, int maxLength) {
        if (text.length() <= maxLength) {
            return text;
        }

        // 查找最后一个自然断点
        String potentialText = text.substring(0, maxLength);

        // 常见断点标记
        String[] breakpoints = { "。", "！", "？", "…", ".", "!", "?", "\n" };

        int lastBreakpoint = -1;
        for (String bp : breakpoints) {
            int lastIndex = potentialText.lastIndexOf(bp);
            if (lastIndex > lastBreakpoint) {
                lastBreakpoint = lastIndex;
            }
        }

        // 如果找到断点，在断点处截断
        if (lastBreakpoint > 0) {
            return potentialText.substring(0, lastBreakpoint + 1);
        }

        // 如果找不到断点，尝试在最后一个完整词或句子处截断
        int lastSpace = potentialText.lastIndexOf(" ");
        if (lastSpace > maxLength * 0.8) { // 如果空格位置至少在预期长度的80%处
            return potentialText.substring(0, lastSpace);
        }

        // 如果以上方法都找不到合适的断点，直接截断并添加省略号
        return potentialText + "...";
    }

    /**
     * 修复常见文本问题
     */
    private String fixCommonTextIssues(String text) {
        if (text == null) {
            return "";
        }

        // 删除多余空白
        String result = text.trim().replaceAll("\\s+", " ");

        // 修复标点符号问题
        result = result.replaceAll("([。！？；：，、])+", "$1");

        // 修复引号不配对问题
        result = fixUnpairedQuotes(result);

        return result;
    }

    /**
     * 修复不配对的引号
     */
    private String fixUnpairedQuotes(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }

        // 计算中文引号数量
        int openChineseCount = 0;
        int closeChineseCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') {
                openChineseCount++;
            } else if (c == '"') {
                closeChineseCount++;
            }
        }

        // 计算英文引号数量
        int doubleQuotesCount = 0;
        int singleQuotesCount = 0;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '"') {
                doubleQuotesCount++;
            } else if (c == '\'') {
                singleQuotesCount++;
            }
        }

        StringBuilder result = new StringBuilder(text);

        // 修复中文引号不配对
        if (openChineseCount > closeChineseCount) {
            for (int i = 0; i < openChineseCount - closeChineseCount; i++) {
                result.append('"');
            }
        }

        // 修复英文双引号不配对（奇数个）
        if (doubleQuotesCount % 2 != 0) {
            result.append('"');
        }

        // 修复英文单引号不配对（奇数个）
        if (singleQuotesCount % 2 != 0) {
            result.append('\'');
        }

        return result.toString();
    }

    /**
     * 获取备用回复
     */
    private String getFallbackResponse(ResponseStrategy.StrategyType strategyType) {
        // 根据不同的策略类型提供不同的备用回复
        String[] responses;

        switch (strategyType) {
            case HIGH_ENGAGEMENT:
                responses = new String[] {
                        "这个话题真的很有趣！你能再多分享一些你的想法吗？我很想听更多。",
                        "哇，我很喜欢你的观点！这让我想到了一些新的角度，你平时还会关注哪些相关的事情？",
                        "真的很棒！我完全能理解你的感受。我也有过类似的经历，让我印象特别深刻。"
                };
                break;

            case MEDIUM_HIGH_ENGAGEMENT:
                responses = new String[] {
                        "嗯，我理解你的意思。这确实是个值得思考的话题，你平时是怎么看待这个问题的？",
                        "这个观点很有意思...我自己也思考过类似的问题。你觉得这会对日常生活有什么影响吗？",
                        "我能理解你说的情况。说起来，最近有遇到其他让你印象深刻的事情吗？"
                };
                break;

            case MEDIUM_ENGAGEMENT:
                responses = new String[] {
                        "我明白了。这个话题其实挺复杂的，每个人可能都有不同的看法。",
                        "这样啊...确实是这样。最近有什么其他有趣的事情想聊聊吗？",
                        "嗯，有道理。我觉得生活中很多事情都是这样，需要我们去平衡和思考。"
                };
                break;

            case LOW_ENGAGEMENT:
                responses = new String[] {
                        "我明白你的意思了。要不我们换个轻松点的话题聊聊？",
                        "嗯...我懂。最近有什么让你开心的事情吗？",
                        "好的，我知道了。有什么我能帮到你的吗？"
                };
                break;

            case VERY_LOW_ENGAGEMENT:
                responses = new String[] {
                        "我理解。需要休息一下吗？",
                        "嗯，好的。有什么需要我帮忙的，随时告诉我。",
                        "明白了。我们可以改天再聊。"
                };
                break;

            default:
                responses = new String[] {
                        "我明白了。有什么其他想聊的话题吗？",
                        "谢谢分享。我们可以继续这个话题，或者聊些别的。",
                        "嗯，有道理。你还有什么想法想分享吗？"
                };
        }

        // 随机选择一个备用回复
        return responses[new Random().nextInt(responses.length)];
    }
}