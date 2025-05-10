package com.dating.ai.dao;

import com.dating.ai.domain.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for ChatMessage entity
 *
 * @author dating-ai
 */
@Repository
public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    /**
     * Find recent messages by user ID
     *
     * @param userId User ID
     * @param limit  Maximum number of messages to return
     * @return List of recent messages
     */
    @Query(sort = "{ createTime: -1 }")
    List<ChatMessage> findRecentMessagesByUserId(String userId, int limit);

    /**
     * Find messages by user ID and conversation ID
     *
     * @param userId         User ID
     * @param conversationId Conversation ID
     * @param pageable       Pagination information
     * @return Page of messages
     */
    Page<ChatMessage> findByUserIdAndConversationIdOrderByCreateTimeDesc(String userId, String conversationId,
            Pageable pageable);

    /**
     * Find messages by user ID
     *
     * @param userId   User ID
     * @param pageable Pagination information
     * @return Page of messages
     */
    Page<ChatMessage> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);

    /**
     * Find messages by user ID and topic
     *
     * @param userId   User ID
     * @param topic    Topic
     * @param pageable Pagination information
     * @return Page of messages
     */
    Page<ChatMessage> findByUserIdAndTopicOrderByCreateTimeDesc(String userId, String topic, Pageable pageable);

    /**
     * Delete all messages by conversation ID
     *
     * @param conversationId Conversation ID
     */
    void deleteByConversationId(String conversationId);
}