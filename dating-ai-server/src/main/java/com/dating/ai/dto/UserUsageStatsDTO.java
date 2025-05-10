package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;

/**
 * Data Transfer Object for User Usage Statistics
 *
 * @author dating-ai
 */
@Data
@Schema(description = "User usage statistics")
public class UserUsageStatsDTO {

    @Schema(description = "Total number of generations")
    private Integer totalGeneration;

    @Schema(description = "Total number of copied messages")
    private Integer totalCopied;

    @Schema(description = "Total number of liked messages")
    private Integer totalLiked;

    @Schema(description = "Total number of disliked messages")
    private Integer totalDisliked;

    @Schema(description = "Most used styles")
    private List<StyleUsageDTO> usedStyles;

    @Schema(description = "Daily limit information")
    private DailyLimitDTO dailyLimit;

    @Schema(description = "Periodic statistics")
    private List<PeriodicStatDTO> periodicStats;

    /**
     * Style usage statistics
     */
    @Data
    @Schema(description = "Style usage statistics")
    public static class StyleUsageDTO {

        @Schema(description = "Style ID")
        private String styleId;

        @Schema(description = "Style name")
        private String name;

        @Schema(description = "Number of times the style was used")
        private Integer count;

        @Schema(description = "Percentage of total usage")
        private Double percentage;
    }

    /**
     * Daily limit information
     */
    @Data
    @Schema(description = "Daily limit information")
    public static class DailyLimitDTO {

        @Schema(description = "Maximum number of generations per day")
        private Integer limit;

        @Schema(description = "Number of generations used today")
        private Integer used;

        @Schema(description = "Number of generations remaining today")
        private Integer remaining;

        @Schema(description = "Reset time for the daily limit")
        private String resetTime;
    }

    /**
     * Periodic statistics
     */
    @Data
    @Schema(description = "Periodic statistics")
    public static class PeriodicStatDTO {

        @Schema(description = "Date")
        private String date;

        @Schema(description = "Number of generations on this date")
        private Integer generation;

        @Schema(description = "Number of copied messages on this date")
        private Integer copied;

        @Schema(description = "Number of liked messages on this date")
        private Integer liked;
    }
}