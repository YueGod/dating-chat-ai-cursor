package com.dating.ai.dao;

import com.dating.ai.domain.ReplyStyle;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for ReplyStyle entity
 *
 * @author dating-ai
 */
@Repository
public interface ReplyStyleRepository extends MongoRepository<ReplyStyle, String> {

    /**
     * Find reply style by style ID
     *
     * @param styleId Style ID
     * @return Optional of ReplyStyle
     */
    Optional<ReplyStyle> findByStyleId(String styleId);

    /**
     * Find reply styles by category
     *
     * @param category Category
     * @param sort     Sort information
     * @return List of reply styles
     */
    List<ReplyStyle> findByCategory(String category, Sort sort);

    /**
     * Find reply styles by VIP status
     *
     * @param isVipOnly VIP only flag
     * @param sort      Sort information
     * @return List of reply styles
     */
    List<ReplyStyle> findByIsVipOnly(Boolean isVipOnly, Sort sort);

    /**
     * Find reply styles by category and VIP status
     *
     * @param category  Category
     * @param isVipOnly VIP only flag
     * @param sort      Sort information
     * @return List of reply styles
     */
    List<ReplyStyle> findByCategoryAndIsVipOnly(String category, Boolean isVipOnly, Sort sort);
}