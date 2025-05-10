package com.dating.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 情绪分析结果
 * 用于存储消息情绪分析的多维度评分和结果
 *
 * @author dating-ai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmotionAnalysisResult {

    /**
     * 总情绪分数 (0-100)
     */
    private int totalScore;

    /**
     * 情感词汇分数 (0-100)
     */
    private int sentimentScore;

    /**
     * 句法结构分数 (0-100)
     */
    private int syntaxScore;

    /**
     * 响应时间分数 (0-100)
     */
    private int responseTimeScore;

    /**
     * 对话连贯性分数 (0-100)
     */
    private int coherenceScore;

    /**
     * 符号使用分数 (0-100)
     */
    private int symbolScore;

    /**
     * 分析置信度 (0.0-1.0)
     */
    private double confidence;

    /**
     * 情绪状态
     */
    private EmotionState emotionalState;

    /**
     * 互动意愿等级
     */
    private InteractionLevel interactionLevel;

    /**
     * 主要意图
     */
    private String primaryIntent;

    /**
     * 识别的主题关键词
     */
    private String[] topics;

    /**
     * 是否需要深度分析
     */
    private boolean needsDeepAnalysis;

    /**
     * 是否存在歧义
     */
    private boolean isAmbiguous;

    /**
     * 根据分数获取情绪状态
     * 
     * @return 情绪状态枚举
     */
    public EmotionState getEmotionalState() {
        if (emotionalState != null) {
            return emotionalState;
        }

        if (totalScore >= 80)
            return EmotionState.EXCITED;
        if (totalScore >= 60)
            return EmotionState.INTERESTED;
        if (totalScore >= 40)
            return EmotionState.NEUTRAL;
        if (totalScore >= 20)
            return EmotionState.TIRED;
        return EmotionState.NEGATIVE;
    }

    /**
     * 根据分数获取互动意愿等级
     * 
     * @return 互动意愿等级枚举
     */
    public InteractionLevel getInteractionLevel() {
        if (interactionLevel != null) {
            return interactionLevel;
        }

        if (totalScore >= 80)
            return InteractionLevel.VERY_HIGH;
        if (totalScore >= 60)
            return InteractionLevel.HIGH;
        if (totalScore >= 40)
            return InteractionLevel.MEDIUM;
        if (totalScore >= 20)
            return InteractionLevel.LOW;
        return InteractionLevel.VERY_LOW;
    }

    /**
     * 情绪状态枚举
     */
    public enum EmotionState {
        EXCITED, // 兴奋/开心
        INTERESTED, // 愉快/感兴趣
        NEUTRAL, // 平静/中性
        TIRED, // 疲倦/犹豫
        NEGATIVE // 消极/不满
    }

    /**
     * 互动意愿等级枚举
     */
    public enum InteractionLevel {
        VERY_HIGH, // 非常高
        HIGH, // 高
        MEDIUM, // 中等
        LOW, // 低
        VERY_LOW // 非常低
    }
}