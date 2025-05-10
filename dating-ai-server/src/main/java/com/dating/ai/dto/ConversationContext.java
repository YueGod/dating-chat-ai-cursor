package com.dating.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 会话上下文
 * 存储对话历史和相关上下文信息
 *
 * @author dating-ai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConversationContext {

    /**
     * 用户资料
     */
    private UserProfileDTO userProfile;

    /**
     * 最近消息历史
     */
    private List<MessagePair> recentMessages;

    /**
     * 情绪评分历史
     */
    private List<EmotionRecord> emotionHistory;

    /**
     * 当前话题
     */
    private String currentTopic;

    /**
     * 当前话题持续轮数
     */
    private int currentTopicTurns;

    /**
     * 上次响应时间（毫秒时间戳）
     */
    private long lastResponseTime;

    /**
     * 获取上次情绪评分
     * 
     * @return 上次情绪评分，如果没有历史记录则返回默认值50
     */
    public int getLastEmotionScore() {
        if (emotionHistory != null && !emotionHistory.isEmpty()) {
            return emotionHistory.get(0).getTotalScore();
        }
        return 50; // 默认中等分数
    }

    /**
     * 获取最后一条用户消息
     * 
     * @return 最后一条用户消息内容，如果没有则返回空字符串
     */
    public String getLastUserMessage() {
        if (recentMessages != null && !recentMessages.isEmpty()) {
            return recentMessages.get(0).getUserMessage();
        }
        return "";
    }

    /**
     * 对话消息对
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MessagePair {
        /**
         * 消息ID
         */
        private String messageId;

        /**
         * 用户消息
         */
        private String userMessage;

        /**
         * AI回复
         */
        private String aiResponse;

        /**
         * 时间戳（毫秒）
         */
        private long timestamp;

        /**
         * 情绪评分
         */
        private int emotionScore;

        /**
         * 话题
         */
        private String topic;

        /**
         * 元数据
         */
        private Map<String, Object> metadata;
    }

    /**
     * 情绪记录
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EmotionRecord {
        /**
         * 记录ID
         */
        private String recordId;

        /**
         * 用户ID
         */
        private String userId;

        /**
         * 时间戳（毫秒）
         */
        private long timestamp;

        /**
         * 总情绪评分
         */
        private int totalScore;

        /**
         * 情感词汇评分
         */
        private int sentimentScore;

        /**
         * 句法结构评分
         */
        private int syntaxScore;

        /**
         * 响应时间评分
         */
        private int responseTimeScore;

        /**
         * 对话连贯性评分
         */
        private int coherenceScore;

        /**
         * 符号使用评分
         */
        private int symbolScore;
    }
}