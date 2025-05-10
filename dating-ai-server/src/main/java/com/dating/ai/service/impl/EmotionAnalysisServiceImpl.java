package com.dating.ai.service.impl;

import com.dating.ai.dto.ConversationContext;
import com.dating.ai.dto.EmotionAnalysisResult;
import com.dating.ai.service.AIService;
import com.dating.ai.service.EmotionAnalysisService;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Arrays;
import java.util.List;

/**
 * 情绪分析服务实现类
 * 负责分析用户消息的情绪状态、意图和互动意愿
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmotionAnalysisServiceImpl implements EmotionAnalysisService {

    private final AIService aiService;

    /**
     * 情绪分析权重配置
     */
    @Value("#{${dating.ai.chat.ai.emotion-factor-weights}}")
    private Map<String, Double> emotionFactorWeights;

    /**
     * 情绪下降阈值
     */
    @Value("${dating.ai.chat.ai.emotion-drop-threshold:15.0}")
    private double emotionDropThreshold;

    // 正面情绪词列表
    private static final List<String> POSITIVE_WORDS = Arrays.asList(
            "开心", "高兴", "幸福", "快乐", "愉快", "兴奋", "有趣", "好玩", "喜欢", "爱",
            "感谢", "谢谢", "棒", "好", "赞", "优秀", "完美", "厉害", "帅", "漂亮",
            "美", "酷", "强", "哈哈", "嘻嘻", "呵呵", "哈", "嘿", "期待", "惊喜");

    // 负面情绪词列表
    private static final List<String> NEGATIVE_WORDS = Arrays.asList(
            "难过", "伤心", "悲伤", "难受", "痛苦", "不开心", "忧愁", "沮丧", "郁闷", "压抑",
            "烦", "烦躁", "讨厌", "恨", "厌倦", "无聊", "乏味", "无趣", "糟糕", "惨",
            "差", "麻烦", "困难", "累", "困", "倦", "唉", "哎", "唉", "害怕", "担心");

    // 表情符号模式
    private static final Pattern EMOJI_PATTERN = Pattern
            .compile("[\\ud83c\\udc00-\\ud83c\\udfff]|[\\ud83d\\udc00-\\ud83d\\udfff]|[\\u2600-\\u27ff]");

    // 标点符号模式
    private static final Pattern PUNCTUATION_PATTERN = Pattern.compile("[!！?？~～。.…]+");

    // 话题终结词
    private static final List<String> TOPIC_ENDING_TOKENS = Arrays.asList(
            "嗯", "哦", "好的", "好", "行", "知道了", "明白", "是的", "是", "啊", "哈",
            "ok", "OK", "Ok", "噢");

    @Override
    public EmotionAnalysisResult analyzeEmotion(String userInput, ConversationContext context) {
        // 1. 执行基于规则的快速分析
        EmotionAnalysisResult quickResult = quickAnalysis(userInput);

        // 2. 决定是否需要深度分析
        if (needsDeepAnalysis(quickResult, userInput, context)) {
            EmotionAnalysisResult deepResult = deepAnalysis(userInput, context, quickResult);
            // 应用规则修正
            return applyEmotionCorrectionRules(deepResult, context);
        }

        // 应用规则修正
        return applyEmotionCorrectionRules(quickResult, context);
    }

    @Override
    public EmotionAnalysisResult quickAnalysis(String userInput) {
        if (StringUtils.isBlank(userInput)) {
            // 空输入，返回低情绪评分
            return createDefaultEmotionResult(20);
        }

        // 初始化评分
        int sentimentScore = 50; // 中性情感基础分
        int syntaxScore = 50; // 中性句法结构基础分
        int symbolScore = 50; // 中性符号使用基础分

        // 情感词分析
        sentimentScore += analyzeEmotionWords(userInput);

        // 句法分析
        syntaxScore += analyzeSyntax(userInput);

        // 符号使用分析
        symbolScore += analyzeSymbols(userInput);

        // 各维度得分限制在0-100范围内
        sentimentScore = Math.max(0, Math.min(100, sentimentScore));
        syntaxScore = Math.max(0, Math.min(100, syntaxScore));
        symbolScore = Math.max(0, Math.min(100, symbolScore));

        // 由于快速分析没有上下文信息，将连贯性和响应时间设为默认值
        int coherenceScore = 50;
        int responseTimeScore = 50;

        // 计算总分
        int totalScore = calculateTotalScore(sentimentScore, syntaxScore, responseTimeScore, coherenceScore,
                symbolScore);

        // 构造结果对象
        EmotionAnalysisResult result = new EmotionAnalysisResult();
        result.setTotalScore(totalScore);
        result.setSentimentScore(sentimentScore);
        result.setSyntaxScore(syntaxScore);
        result.setResponseTimeScore(responseTimeScore);
        result.setCoherenceScore(coherenceScore);
        result.setSymbolScore(symbolScore);
        result.setConfidence(calculateConfidence(userInput));
        result.setNeedsDeepAnalysis(false);
        result.setAmbiguous(false);

        // 识别主题关键词
        String[] topics = extractTopics(userInput);
        result.setTopics(topics);

        // 识别主要意图
        String intent = analyzeIntent(userInput);
        result.setPrimaryIntent(intent);

        return result;
    }

    @Override
    public EmotionAnalysisResult deepAnalysis(String userInput, ConversationContext context,
            EmotionAnalysisResult quickResult) {
        try {
            // 构建情感分析提示词
            String prompt = buildEmotionAnalysisPrompt(userInput, context, quickResult);

            // 调用AI获取详细情感分析
            String aiResponse = aiService.generateChatReply(prompt, "emotion_analysis", getSystemPrompt());

            // 解析AI响应
            return parseEmotionAnalysisResponse(aiResponse, quickResult);
        } catch (Exception e) {
            log.error("深度情感分析失败: {}", e.getMessage(), e);
            // 发生错误时返回快速分析结果
            return quickResult;
        }
    }

    @Override
    public EmotionAnalysisResult applyEmotionCorrectionRules(EmotionAnalysisResult result,
            ConversationContext context) {
        if (context == null || context.getEmotionHistory() == null || context.getEmotionHistory().isEmpty()) {
            // 无历史数据可参考，直接返回原结果
            return result;
        }

        // 获取历史情绪记录
        List<ConversationContext.EmotionRecord> history = context.getEmotionHistory();

        // 从历史记录中获取最后一次情绪分析结果
        int lastScore = history.get(0).getTotalScore();

        // 如果情绪分数变化太大（超过设定阈值），进行平滑处理
        int currentScore = result.getTotalScore();
        if (Math.abs(currentScore - lastScore) > emotionDropThreshold) {
            // 平滑处理：向历史分数靠近30%
            int smoothedScore = currentScore + (int) ((lastScore - currentScore) * 0.3);
            result.setTotalScore(smoothedScore);

            log.debug("情绪分数平滑处理: 原始分数={}, 历史分数={}, 平滑后分数={}",
                    currentScore, lastScore, smoothedScore);
        }

        // 持续性判断：如果用户连续回复以表达中性或消极情绪，增加降低分数
        int consecutiveNegativeCount = countConsecutiveNegativeEmotions(history);
        if (consecutiveNegativeCount >= 2 && result.getTotalScore() < 60) {
            // 连续负面情绪，进一步降低分数
            int adjustedScore = Math.max(0, result.getTotalScore() - (consecutiveNegativeCount * 5));
            result.setTotalScore(adjustedScore);

            log.debug("连续负面情绪调整: 连续次数={}, 调整后分数={}",
                    consecutiveNegativeCount, adjustedScore);
        }

        return result;
    }

    /**
     * 判断是否需要深度分析
     */
    private boolean needsDeepAnalysis(EmotionAnalysisResult quickResult, String userInput,
            ConversationContext context) {
        // 如果快速分析结果已标记为需要深度分析
        if (quickResult.isNeedsDeepAnalysis()) {
            return true;
        }

        // 当置信度较低时
        if (quickResult.getConfidence() < 0.7) {
            return true;
        }

        // 消息较长时
        if (userInput.length() > 100) {
            return true;
        }

        // 情感分析结果在边缘区域（接近情绪状态变化的阈值）
        int score = quickResult.getTotalScore();
        if ((score >= 75 && score <= 85) ||
                (score >= 55 && score <= 65) ||
                (score >= 35 && score <= 45) ||
                (score >= 15 && score <= 25)) {
            return true;
        }

        // 如果上下文中有急剧的情绪变化
        if (context != null && context.getEmotionHistory() != null &&
                !context.getEmotionHistory().isEmpty()) {
            int lastScore = context.getEmotionHistory().get(0).getTotalScore();
            if (Math.abs(score - lastScore) > emotionDropThreshold) {
                return true;
            }
        }

        return false;
    }

    /**
     * 构建情感分析提示词
     */
    private String buildEmotionAnalysisPrompt(String userInput, ConversationContext context,
            EmotionAnalysisResult quickResult) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("分析以下用户输入的情绪状态，根据情感词汇(40%)、")
                .append("句法结构(20%)、反应速度(15%)、")
                .append("对话连贯性(15%)和符号使用(10%)五个维度评分(0-100)：\n\n");

        prompt.append("用户输入: \"").append(userInput).append("\"\n");

        // 添加对话历史上下文
        if (context != null && context.getRecentMessages() != null && !context.getRecentMessages().isEmpty()) {
            prompt.append("对话历史: \n");
            List<ConversationContext.MessagePair> recentMessages = context.getRecentMessages();
            // 最多包含3轮历史消息
            int historySize = Math.min(recentMessages.size(), 3);
            for (int i = 0; i < historySize; i++) {
                ConversationContext.MessagePair message = recentMessages.get(i);
                prompt.append("- 用户: \"").append(message.getUserMessage()).append("\"\n");
                if (message.getAiResponse() != null) {
                    prompt.append("- 助手: \"").append(message.getAiResponse()).append("\"\n");
                }
            }
        }

        // 添加时间信息
        if (context != null) {
            long currentTime = System.currentTimeMillis();
            prompt.append("上次响应时间: ").append(context.getLastResponseTime()).append("\n");
            prompt.append("当前时间: ").append(currentTime).append("\n");
            prompt.append("时间差(毫秒): ").append(currentTime - context.getLastResponseTime()).append("\n");
        }

        // 添加快速分析结果
        prompt.append("\n快速分析结果: \n");
        prompt.append("- 情感词汇分数: ").append(quickResult.getSentimentScore()).append("\n");
        prompt.append("- 句法结构分数: ").append(quickResult.getSyntaxScore()).append("\n");
        prompt.append("- 符号使用分数: ").append(quickResult.getSymbolScore()).append("\n");

        // 输出格式说明
        prompt.append("\n请提供详细的情绪分析，并以JSON格式输出以下内容：\n");
        prompt.append("1. totalScore: 总情绪分数(0-100)\n");
        prompt.append("2. sentimentScore: 情感词汇分数(0-100)\n");
        prompt.append("3. syntaxScore: 句法结构分数(0-100)\n");
        prompt.append("4. responseTimeScore: 反应速度分数(0-100)\n");
        prompt.append("5. coherenceScore: 对话连贯性分数(0-100)\n");
        prompt.append("6. symbolScore: 符号使用分数(0-100)\n");
        prompt.append("7. primaryIntent: 主要意图(字符串)\n");
        prompt.append("8. topics: 识别到的主题关键词(字符串数组)\n");
        prompt.append("9. emotionalState: 情绪状态(EXCITED/INTERESTED/NEUTRAL/TIRED/NEGATIVE)\n");
        prompt.append("10. interactionLevel: 互动意愿等级(VERY_HIGH/HIGH/MEDIUM/LOW/VERY_LOW)\n");

        prompt.append("\n仅返回JSON对象，不要有其他文本。");

        return prompt.toString();
    }

    /**
     * 获取系统提示词
     */
    private String getSystemPrompt() {
        return "你是一个专业的情绪分析助手，擅长分析文本中表达的情绪、意图和互动意愿。" +
                "你的分析应该客观、准确，并遵循指定的格式要求。" +
                "不要解释你的分析过程，只返回要求的JSON格式结果。";
    }

    /**
     * 解析情感分析响应
     */
    private EmotionAnalysisResult parseEmotionAnalysisResponse(String aiResponse, EmotionAnalysisResult quickResult) {
        try {
            // 尝试解析JSON响应
            JSONObject jsonResponse = JSON.parseObject(aiResponse);

            EmotionAnalysisResult result = new EmotionAnalysisResult();

            // 提取各个评分字段
            result.setTotalScore(jsonResponse.getIntValue("totalScore", quickResult.getTotalScore()));
            result.setSentimentScore(jsonResponse.getIntValue("sentimentScore", quickResult.getSentimentScore()));
            result.setSyntaxScore(jsonResponse.getIntValue("syntaxScore", quickResult.getSyntaxScore()));
            result.setResponseTimeScore(
                    jsonResponse.getIntValue("responseTimeScore", quickResult.getResponseTimeScore()));
            result.setCoherenceScore(jsonResponse.getIntValue("coherenceScore", quickResult.getCoherenceScore()));
            result.setSymbolScore(jsonResponse.getIntValue("symbolScore", quickResult.getSymbolScore()));

            // 提取意图和主题
            result.setPrimaryIntent(jsonResponse.getString("primaryIntent"));

            // 提取主题数组
            if (jsonResponse.containsKey("topics")) {
                result.setTopics(jsonResponse.getJSONArray("topics").toArray(new String[0]));
            } else {
                result.setTopics(quickResult.getTopics());
            }

            // 提取情绪状态和互动等级
            String emotionalStateStr = jsonResponse.getString("emotionalState");
            String interactionLevelStr = jsonResponse.getString("interactionLevel");

            if (emotionalStateStr != null) {
                try {
                    result.setEmotionalState(EmotionAnalysisResult.EmotionState.valueOf(emotionalStateStr));
                } catch (IllegalArgumentException e) {
                    // 使用基于分数的默认值
                }
            }

            if (interactionLevelStr != null) {
                try {
                    result.setInteractionLevel(EmotionAnalysisResult.InteractionLevel.valueOf(interactionLevelStr));
                } catch (IllegalArgumentException e) {
                    // 使用基于分数的默认值
                }
            }

            // 设置置信度
            result.setConfidence(0.9); // AI分析的置信度较高

            return result;
        } catch (Exception e) {
            log.error("解析情绪分析响应失败: {}", e.getMessage(), e);
            return quickResult; // 发生错误时返回快速分析结果
        }
    }

    /**
     * 计算总情绪分数
     */
    private int calculateTotalScore(int sentimentScore, int syntaxScore, int responseTimeScore,
            int coherenceScore, int symbolScore) {
        // 使用配置的权重计算加权总分
        double weightedSum = sentimentScore * emotionFactorWeights.getOrDefault("sentiment", 0.4) +
                syntaxScore * emotionFactorWeights.getOrDefault("syntax", 0.2) +
                responseTimeScore * emotionFactorWeights.getOrDefault("responseTime", 0.15) +
                coherenceScore * emotionFactorWeights.getOrDefault("coherence", 0.15) +
                symbolScore * emotionFactorWeights.getOrDefault("symbol", 0.1);

        return (int) Math.round(weightedSum);
    }

    /**
     * 分析情感词
     */
    private int analyzeEmotionWords(String text) {
        int score = 0;

        // 转为小写以便匹配
        String lowerText = text.toLowerCase();

        // 计算正面情绪词出现次数
        for (String word : POSITIVE_WORDS) {
            if (lowerText.contains(word)) {
                score += 10;
            }
        }

        // 计算负面情绪词出现次数
        for (String word : NEGATIVE_WORDS) {
            if (lowerText.contains(word)) {
                score -= 10;
            }
        }

        return score;
    }

    /**
     * 分析句法结构
     */
    private int analyzeSyntax(String text) {
        int score = 0;

        // 句子长度分析
        if (text.length() < 5) {
            // 非常短的消息，降低分数
            score -= 20;
        } else if (text.length() < 10) {
            // 短消息，轻微降低分数
            score -= 10;
        } else if (text.length() > 100) {
            // 长消息，提高分数
            score += 15;
        } else if (text.length() > 50) {
            // 中等长度消息，轻微提高分数
            score += 10;
        }

        // 问题分析
        int questionCount = countOccurrences(text, "?") + countOccurrences(text, "？");
        if (questionCount > 0) {
            // 包含问题，提高分数
            score += 10 * Math.min(questionCount, 3);
        }

        // 是否只包含终结词
        if (isOnlyTopicEndingToken(text)) {
            score -= 30;
        }

        return score;
    }

    /**
     * 分析符号使用
     */
    private int analyzeSymbols(String text) {
        int score = 0;

        // 表情符号分析
        Matcher emojiMatcher = EMOJI_PATTERN.matcher(text);
        int emojiCount = 0;
        while (emojiMatcher.find()) {
            emojiCount++;
        }

        if (emojiCount > 0) {
            // 使用表情符号，提高分数，但最多加30分
            score += Math.min(emojiCount * 10, 30);
        }

        // 强调性标点符号分析
        Matcher punctuationMatcher = PUNCTUATION_PATTERN.matcher(text);
        int punctuationCount = 0;
        while (punctuationMatcher.find()) {
            punctuationCount++;
        }

        if (punctuationCount > 0) {
            // 使用强调性标点，提高分数，但最多加20分
            score += Math.min(punctuationCount * 5, 20);
        }

        return score;
    }

    /**
     * 计算文本中特定字符的出现次数
     */
    private int countOccurrences(String text, String target) {
        return text.length() - text.replace(target, "").length();
    }

    /**
     * 判断文本是否只包含话题终结词
     */
    private boolean isOnlyTopicEndingToken(String text) {
        String trimmed = text.trim();
        for (String token : TOPIC_ENDING_TOKENS) {
            if (trimmed.equals(token)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 提取主题关键词
     */
    private String[] extractTopics(String text) {
        // 简单实现：取最长的几个词作为主题词
        // 实际实现中可以使用更复杂的NLP技术
        String[] words = text.split("\\s+");
        if (words.length <= 3) {
            return words;
        }

        // 取最长的3个词
        Arrays.sort(words, (a, b) -> Integer.compare(b.length(), a.length()));
        return Arrays.copyOf(words, 3);
    }

    /**
     * 分析用户意图
     */
    private String analyzeIntent(String text) {
        // 简单意图分析
        if (text.contains("?") || text.contains("？")) {
            return "question";
        } else if (text.length() < 10 && TOPIC_ENDING_TOKENS.contains(text.trim())) {
            return "acknowledgment";
        } else if (text.length() > 50) {
            return "statement";
        } else {
            return "chat";
        }
    }

    /**
     * 计算分析置信度
     */
    private double calculateConfidence(String text) {
        // 简单实现：基于消息长度和明确性估算置信度
        if (text.length() < 5) {
            return 0.5; // 短消息，低置信度
        } else if (text.length() < 20) {
            return 0.6;
        } else if (text.length() < 50) {
            return 0.7;
        } else {
            return 0.8; // 长消息，较高置信度
        }
    }

    /**
     * 创建默认的情绪分析结果
     */
    private EmotionAnalysisResult createDefaultEmotionResult(int baseScore) {
        EmotionAnalysisResult result = new EmotionAnalysisResult();
        result.setTotalScore(baseScore);
        result.setSentimentScore(baseScore);
        result.setSyntaxScore(baseScore);
        result.setResponseTimeScore(50);
        result.setCoherenceScore(50);
        result.setSymbolScore(baseScore);
        result.setConfidence(0.5);
        return result;
    }

    /**
     * 计算连续负面情绪次数
     */
    private int countConsecutiveNegativeEmotions(List<ConversationContext.EmotionRecord> history) {
        int count = 0;
        for (ConversationContext.EmotionRecord record : history) {
            if (record.getTotalScore() < 40) {
                count++;
            } else {
                break;
            }
        }
        return count;
    }
}