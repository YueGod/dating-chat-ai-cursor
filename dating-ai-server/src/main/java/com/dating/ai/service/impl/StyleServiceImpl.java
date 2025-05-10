package com.dating.ai.service.impl;

import com.dating.ai.dao.ReplyStyleRepository;
import com.dating.ai.dao.UserMembershipRepository;
import com.dating.ai.domain.ReplyStyle;
import com.dating.ai.service.StyleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of the StyleService interface
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class StyleServiceImpl implements StyleService {

    private final ReplyStyleRepository styleRepository;
    private final UserMembershipRepository membershipRepository;

    // TODO: Replace with actual user context mechanism
    private static final String CURRENT_USER_ID = "user123456";

    @Override
    public Map<String, Object> getStyles(String category, Boolean isVipOnly) {
        Map<String, Object> result = new HashMap<>();

        // Determine if user is VIP
        boolean userIsVip = membershipRepository.findByUserIdAndIsActiveTrue(CURRENT_USER_ID).isPresent();

        // Prepare query based on parameters
        List<ReplyStyle> styles;
        Sort sort = Sort.by(Sort.Direction.ASC, "displayOrder");

        if (category != null && isVipOnly != null) {
            styles = styleRepository.findByCategoryAndIsVipOnly(category, isVipOnly, sort);
        } else if (category != null) {
            styles = styleRepository.findByCategory(category, sort);
        } else if (isVipOnly != null) {
            styles = styleRepository.findByIsVipOnly(isVipOnly, sort);
        } else {
            styles = styleRepository.findAll(sort);
        }

        // Filter VIP styles if user is not VIP and not explicitly requesting VIP styles
        if (!userIsVip && isVipOnly == null) {
            styles = styles.stream()
                    .filter(style -> !style.getIsVipOnly())
                    .collect(Collectors.toList());
        }

        // Transform styles to map
        List<Map<String, Object>> stylesList = styles.stream()
                .map(this::convertStyleToMap)
                .collect(Collectors.toList());

        // Get categories
        List<Map<String, Object>> categories = getCategories();

        result.put("styles", stylesList);
        result.put("categories", categories);

        return result;
    }

    @Override
    public ReplyStyle getStyleById(String styleId) {
        return styleRepository.findByStyleId(styleId)
                .orElseThrow(() -> new RuntimeException("Style not found: " + styleId));
    }

    @Override
    public List<Map<String, Object>> getCategories() {
        // Get all styles to count by category
        List<ReplyStyle> allStyles = styleRepository.findAll();

        // Count styles by category
        Map<String, Long> categoryCount = allStyles.stream()
                .collect(Collectors.groupingBy(ReplyStyle::getCategory, Collectors.counting()));

        // Create category list
        List<Map<String, Object>> categories = new ArrayList<>();
        categoryCount.forEach((category, count) -> {
            Map<String, Object> categoryMap = new HashMap<>();
            categoryMap.put("id", category.toLowerCase().replace(" ", "_"));
            categoryMap.put("name", category);
            categoryMap.put("count", count);
            categories.add(categoryMap);
        });

        // Sort by display order (using simple alphabetical order here)
        categories.sort(Comparator.comparing(map -> (String) map.get("name")));

        return categories;
    }

    /**
     * Convert ReplyStyle entity to map for API response
     *
     * @param style ReplyStyle entity
     * @return Map containing style information
     */
    private Map<String, Object> convertStyleToMap(ReplyStyle style) {
        Map<String, Object> styleMap = new HashMap<>();
        styleMap.put("styleId", style.getStyleId());
        styleMap.put("name", style.getName());
        styleMap.put("description", style.getDescription());
        styleMap.put("longDescription", style.getLongDescription());
        styleMap.put("tags", style.getTags());
        styleMap.put("category", style.getCategory());
        styleMap.put("popularity", style.getPopularity());
        styleMap.put("isVipOnly", style.getIsVipOnly());
        styleMap.put("iconUrl", style.getIconUrl());

        // Convert examples to simplified format
        if (style.getExamples() != null && !style.getExamples().isEmpty()) {
            List<Map<String, String>> examples = style.getExamples().stream()
                    .map(example -> {
                        Map<String, String> exampleMap = new HashMap<>();
                        exampleMap.put("receivedMessage", example.getReceivedMessage());
                        exampleMap.put("replyExample", example.getReplyExample());
                        return exampleMap;
                    })
                    .collect(Collectors.toList());
            styleMap.put("examples", examples);
        }

        return styleMap;
    }
}