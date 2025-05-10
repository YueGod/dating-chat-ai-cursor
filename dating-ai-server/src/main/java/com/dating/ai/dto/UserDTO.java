package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;

/**
 * Data Transfer Object for User
 *
 * @author dating-ai
 */
@Data
@Schema(description = "User information")
public class UserDTO {

    @Schema(description = "User ID")
    private String userId;

    @Schema(description = "User nickname")
    private String nickname;

    @Schema(description = "User avatar URL")
    private String avatar;

    @Schema(description = "User gender")
    private String gender;

    @Schema(description = "User phone number (masked)")
    private String phone;

    @Schema(description = "User membership information")
    private MembershipDTO membershipInfo;

    @Schema(description = "User statistics")
    private StatisticsDTO statistics;

    @Schema(description = "User settings")
    private SettingsDTO settings;

    /**
     * User membership information
     */
    @Data
    @Schema(description = "User membership information")
    public static class MembershipDTO {

        @Schema(description = "Membership level")
        private Integer level;

        @Schema(description = "Whether membership is active")
        private Boolean isActive;

        @Schema(description = "Membership expiration date")
        private Date expireDate;

        @Schema(description = "Days remaining until expiration")
        private Integer daysRemaining;

        @Schema(description = "Whether auto-renewal is enabled")
        private Boolean autoRenew;

        @Schema(description = "Membership benefits")
        private String[] benefits;
    }

    /**
     * User statistics
     */
    @Data
    @Schema(description = "User statistics")
    public static class StatisticsDTO {

        @Schema(description = "Total usage count")
        private Integer totalUsage;

        @Schema(description = "Favorite styles")
        private String[] favoriteStyles;

        @Schema(description = "Success rate percentage")
        private Integer successRate;
    }

    /**
     * User settings
     */
    @Data
    @Schema(description = "User settings")
    public static class SettingsDTO {

        @Schema(description = "Whether notifications are enabled")
        private Boolean notification;

        @Schema(description = "Privacy level")
        private String privacy;

        @Schema(description = "Chat history retention period in days")
        private Integer chatHistoryRetention;

        @Schema(description = "User interface language")
        private String language;
    }
}