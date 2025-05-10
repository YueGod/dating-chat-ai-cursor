package com.dating.ai.service;

import com.dating.ai.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for chat functionality
 *
 * @author dating-ai
 */
public interface ChatService {

    /**
     * Process a user message and generate an AI response
     *
     * @param userId  User ID
     * @param message User message
     * @return AI response
     */
    ChatMessage processUserMessage(String userId, String message);

    /**
     * Get chat history for a user
     *
     * @param userId         User ID
     * @param conversationId Conversation ID (optional)
     * @param pageable       Pagination information
     * @return Page of chat messages
     */
    Page<ChatMessage> getChatHistory(String userId, String conversationId, Pageable pageable);

    /**
     * Delete a conversation
     *
     * @param userId         User ID
     * @param conversationId Conversation ID
     */
    void deleteConversation(String userId, String conversationId);

    /**
     * Start a new conversation
     *
     * @param userId User ID
     * @return New conversation ID
     */
    String startNewConversation(String userId);
}