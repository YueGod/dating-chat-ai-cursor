package com.dating.ai.service;

import com.dating.ai.dto.ConversationContext;
import com.dating.ai.dto.EmotionAnalysisResult;

/**
 * 情绪分析服务
 * 负责分析用户消息的情绪状态、意图和互动意愿
 *
 * @author dating-ai
 */
public interface EmotionAnalysisService {

    /**
     * 分析用户输入的情绪状态
     *
     * @param userInput 用户输入
     * @param context   会话上下文
     * @return 情绪分析结果
     */
    EmotionAnalysisResult analyzeEmotion(String userInput, ConversationContext context);

    /**
     * 快速分析情绪状态（基于关键词和语法规则）
     *
     * @param userInput 用户输入
     * @return 简单情绪分析结果
     */
    EmotionAnalysisResult quickAnalysis(String userInput);

    /**
     * 深度分析情绪状态（基于AI模型）
     *
     * @param userInput   用户输入
     * @param context     会话上下文
     * @param quickResult 快速分析结果
     * @return 详细情绪分析结果
     */
    EmotionAnalysisResult deepAnalysis(String userInput, ConversationContext context,
            EmotionAnalysisResult quickResult);

    /**
     * 应用情绪规则进行修正
     *
     * @param result  原始分析结果
     * @param context 会话上下文
     * @return 修正后的分析结果
     */
    EmotionAnalysisResult applyEmotionCorrectionRules(EmotionAnalysisResult result, ConversationContext context);
}