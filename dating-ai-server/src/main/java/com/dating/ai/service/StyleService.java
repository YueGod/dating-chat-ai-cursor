package com.dating.ai.service;

import com.dating.ai.domain.ReplyStyle;

import java.util.List;
import java.util.Map;

/**
 * Service interface for reply style operations
 *
 * @author dating-ai
 */
public interface StyleService {

    /**
     * Get available reply styles
     *
     * @param category  Optional category filter
     * @param isVipOnly Optional VIP-only filter
     * @return Map containing styles and categories
     */
    Map<String, Object> getStyles(String category, Boolean isVipOnly);

    /**
     * Get a specific reply style by ID
     *
     * @param styleId Style ID
     * @return Reply style
     */
    ReplyStyle getStyleById(String styleId);

    /**
     * Get categories for reply styles
     *
     * @return List of category information
     */
    List<Map<String, Object>> getCategories();
}