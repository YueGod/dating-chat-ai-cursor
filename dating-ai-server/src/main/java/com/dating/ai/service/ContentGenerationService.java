package com.dating.ai.service;

import com.dating.ai.dto.ConversationContext;
import com.dating.ai.dto.EmotionAnalysisResult;
import com.dating.ai.dto.ResponseStrategy;

/**
 * 内容生成服务
 * 负责根据情绪分析和策略生成AI回复内容
 *
 * @author dating-ai
 */
public interface ContentGenerationService {

    /**
     * 根据策略生成回复内容
     *
     * @param userInput 用户输入
     * @param emotion   情绪分析结果
     * @param strategy  回复策略
     * @param context   会话上下文
     * @return 生成的回复内容
     */
    String generateResponse(String userInput, EmotionAnalysisResult emotion,
            ResponseStrategy strategy, ConversationContext context);

    /**
     * 构建响应生成提示词
     *
     * @param userInput 用户输入
     * @param emotion   情绪分析结果
     * @param strategy  回复策略
     * @param context   会话上下文
     * @return 提示词文本
     */
    String buildResponseGenerationPrompt(String userInput, EmotionAnalysisResult emotion,
            ResponseStrategy strategy, ConversationContext context);

    /**
     * 后处理回复内容
     *
     * @param rawResponse 原始回复
     * @param strategy    回复策略
     * @param context     会话上下文
     * @return 处理后的回复
     */
    String postProcessResponse(String rawResponse, ResponseStrategy strategy,
            ConversationContext context);
}