package com.dating.ai.service;

import com.dating.ai.dto.ChatRequestDTO;
import com.dating.ai.dto.ChatResponseDTO;

import java.util.List;
import java.util.Map;

/**
 * 聊天机器人服务接口
 * 定义聊天机器人的核心功能
 *
 * @author dating-ai
 */
public interface ChatBotService {

    /**
     * 处理聊天请求，生成AI回复
     *
     * @param chatRequest 聊天请求
     * @return 聊天响应
     */
    ChatResponseDTO chat(ChatRequestDTO chatRequest);

    /**
     * 基于语义搜索相关消息
     *
     * @param query          查询文本
     * @param conversationId 会话ID
     * @param limit          返回结果数量限制
     * @return 相关消息列表
     */
    List<Map<String, Object>> searchRelatedMessages(String query, String conversationId, int limit);
}