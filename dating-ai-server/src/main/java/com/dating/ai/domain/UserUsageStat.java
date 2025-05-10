package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * User usage statistics entity
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "user_usage_stats")
public class UserUsageStat extends BaseEntity {

    private String userId;
    private Date date;
    private Integer generationCount;
    private Integer copyCount;
    private Integer likeCount;
    private Integer dislikeCount;
    private Integer dailyLimit;
    private List<StyleUsage> usedStyles;

    /**
     * Style usage statistics
     */
    @Data
    public static class StyleUsage {
        private String styleId;
        private Integer count;
    }
}