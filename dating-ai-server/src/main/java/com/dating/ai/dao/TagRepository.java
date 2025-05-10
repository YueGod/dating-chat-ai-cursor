package com.dating.ai.dao;

import com.dating.ai.domain.Tag;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Tag entity
 *
 * @author dating-ai
 */
@Repository
public interface TagRepository extends MongoRepository<Tag, String> {

    /**
     * Find tag by ID
     *
     * @param tagId Tag ID
     * @return Optional of Tag
     */
    Optional<Tag> findByTagId(String tagId);

    /**
     * Find tags by user ID
     *
     * @param userId User ID
     * @param sort   Sort information
     * @return List of tags
     */
    List<Tag> findByUserId(String userId, Sort sort);

    /**
     * Find tag by user ID and name
     *
     * @param userId User ID
     * @param name   Tag name
     * @return Optional of Tag
     */
    Optional<Tag> findByUserIdAndName(String userId, String name);

    /**
     * Delete tag by ID and user ID
     *
     * @param tagId  Tag ID
     * @param userId User ID
     */
    void deleteByTagIdAndUserId(String tagId, String userId);
}