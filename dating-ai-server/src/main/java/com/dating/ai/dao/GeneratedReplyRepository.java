package com.dating.ai.dao;

import com.dating.ai.domain.GeneratedReply;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for GeneratedReply entity
 *
 * @author dating-ai
 */
@Repository
public interface GeneratedReplyRepository extends MongoRepository<GeneratedReply, String> {

    /**
     * Find generated reply by request ID
     *
     * @param requestId Request ID
     * @return Optional of GeneratedReply
     */
    Optional<GeneratedReply> findByRequestId(String requestId);

    /**
     * Find generated replies by user ID
     *
     * @param userId   User ID
     * @param pageable Pagination information
     * @return Page of generated replies
     */
    Page<GeneratedReply> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);

    /**
     * Find generated reply by conversation ID and received message ID
     *
     * @param conversationId    Conversation ID
     * @param receivedMessageId Received message ID
     * @return Optional of GeneratedReply
     */
    Optional<GeneratedReply> findByConversationIdAndReceivedMessageId(
            String conversationId, String receivedMessageId);
}