package com.dating.ai.service;

import java.util.List;
import java.util.Map;

/**
 * AI服务接口
 * 提供AI模型调用和向量嵌入功能
 *
 * @author dating-ai
 */
public interface AIService {

    /**
     * 生成AI聊天回复
     *
     * @param userInput      用户输入
     * @param conversationId 会话ID
     * @param systemPrompt   系统提示（可选）
     * @return AI生成的回复文本
     */
    String generateChatReply(String userInput, String conversationId, String systemPrompt);

    /**
     * 生成AI聊天回复（带历史记录）
     *
     * @param userInput      用户输入
     * @param conversationId 会话ID
     * @param systemPrompt   系统提示（可选）
     * @param history        对话历史记录
     * @return AI生成的回复文本
     */
    String generateChatReply(String userInput, String conversationId, String systemPrompt,
            List<Map<String, String>> history);

    /**
     * 生成文本嵌入向量
     *
     * @param text 需要嵌入的文本
     * @return 生成的嵌入向量
     */
    List<Float> generateEmbedding(String text);

    /**
     * 批量生成文本嵌入向量
     *
     * @param texts 需要嵌入的文本列表
     * @return 生成的嵌入向量列表
     */
    List<List<Float>> generateEmbeddings(List<String> texts);
}