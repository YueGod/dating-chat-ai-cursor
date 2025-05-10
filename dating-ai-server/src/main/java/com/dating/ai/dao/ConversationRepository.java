package com.dating.ai.dao;

import com.dating.ai.domain.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.Optional;

/**
 * Repository for Conversation entity
 *
 * @author dating-ai
 */
@Repository
public interface ConversationRepository extends MongoRepository<Conversation, String> {

    /**
     * Find conversation by conversation ID
     *
     * @param conversationId Conversation ID
     * @return Optional of Conversation
     */
    Optional<Conversation> findByConversationId(String conversationId);

    /**
     * Find conversations by user ID and status and updated after the specified date
     *
     * @param userId      User ID
     * @param status      Status
     * @param lastUpdated Last updated date
     * @param pageable    Pagination information
     * @return Page of conversations
     */
    Page<Conversation> findByUserIdAndStatusAndUpdateTimeGreaterThan(
            String userId, String status, Date lastUpdated, Pageable pageable);

    /**
     * Find conversations by user ID and status and containing the specified tag
     *
     * @param userId   User ID
     * @param status   Status
     * @param tag      Tag
     * @param pageable Pagination information
     * @return Page of conversations
     */
    Page<Conversation> findByUserIdAndStatusAndTagsContaining(
            String userId, String status, String tag, Pageable pageable);

    /**
     * Count conversations by user ID and status
     *
     * @param userId User ID
     * @param status Status
     * @return Count of conversations
     */
    long countByUserIdAndStatus(String userId, String status);

    /**
     * Count starred conversations by user ID and status
     *
     * @param userId User ID
     * @param status Status
     * @return Count of starred conversations
     */
    long countByUserIdAndStatusAndIsStarredTrue(String userId, String status);

    /**
     * Find all conversations by user ID and status
     * 
     * @param userId   User ID
     * @param status   Status
     * @param pageable Pagination information
     * @return Page of conversations
     */
    Page<Conversation> findByUserIdAndStatus(String userId, String status, Pageable pageable);
}