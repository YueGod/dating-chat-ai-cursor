package com.dating.ai.service;

import com.dating.ai.dto.ConversationContext;
import com.dating.ai.dto.EmotionAnalysisResult;
import com.dating.ai.dto.ResponseStrategy;

/**
 * 回复策略服务
 * 根据情绪分析和会话上下文确定回复策略
 *
 * @author dating-ai
 */
public interface ResponseStrategyService {

    /**
     * 确定回复策略
     *
     * @param emotion 情绪分析结果
     * @param context 会话上下文
     * @return 回复策略
     */
    ResponseStrategy determineStrategy(EmotionAnalysisResult emotion, ConversationContext context);

    /**
     * 确定是否需要话题切换
     *
     * @param emotion 情绪分析结果
     * @param context 会话上下文
     * @return 是否需要话题切换
     */
    boolean determineTopicSwitchNeed(EmotionAnalysisResult emotion, ConversationContext context);

    /**
     * 选择新话题
     *
     * @param context      会话上下文
     * @param emotionScore 情绪分数
     * @return 新话题
     */
    String selectNewTopic(ConversationContext context, int emotionScore);

    /**
     * 选择个性化元素
     *
     * @param emotion 情绪分析结果
     * @param context 会话上下文
     * @return 个性化元素列表
     */
    java.util.List<ResponseStrategy.PersonalizationElement> selectPersonalizationElements(
            EmotionAnalysisResult emotion, ConversationContext context);
}