package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object for User Limits
 *
 * @author dating-ai
 */
@Data
@Schema(description = "User limits information")
public class UserLimitsDTO {

    @Schema(description = "Daily generation limits")
    private DailyGenerationDTO dailyGeneration;

    @Schema(description = "Accessible styles information")
    private AccessibleStylesDTO accessibleStyles;

    @Schema(description = "Conversation retention information")
    private ConversationRetentionDTO conversationRetention;

    @Schema(description = "VIP upgrade information")
    private VipUpgradeInfoDTO vipUpgradeInfo;

    /**
     * Daily generation limit information
     */
    @Data
    @Schema(description = "Daily generation limit information")
    public static class DailyGenerationDTO {

        @Schema(description = "Maximum number of generations per day")
        private Integer limit;

        @Schema(description = "Number of generations used today")
        private Integer used;

        @Schema(description = "Number of generations remaining today")
        private Integer remaining;

        @Schema(description = "Reset time for the daily limit")
        private Date resetTime;
    }

    /**
     * Accessible styles information
     */
    @Data
    @Schema(description = "Accessible styles information")
    public static class AccessibleStylesDTO {

        @Schema(description = "Total number of styles")
        private Integer total;

        @Schema(description = "Number of styles accessible to the user")
        private Integer accessible;

        @Schema(description = "Number of styles only accessible to VIP users")
        private Integer vipOnly;
    }

    /**
     * Conversation retention information
     */
    @Data
    @Schema(description = "Conversation retention information")
    public static class ConversationRetentionDTO {

        @Schema(description = "Number of days conversations are retained for regular users")
        private Integer days;

        @Schema(description = "Number of days conversations are retained for VIP users")
        private Integer vipDays;
    }

    /**
     * VIP upgrade information
     */
    @Data
    @Schema(description = "VIP upgrade information")
    public static class VipUpgradeInfoDTO {

        @Schema(description = "Sample benefits of VIP membership")
        private List<String> sampleBenefits;

        @Schema(description = "Recommended plan ID")
        private String recommendedPlan;

        @Schema(description = "Current discount information")
        private String discount;
    }
}