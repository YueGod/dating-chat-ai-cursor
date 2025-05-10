package com.dating.ai.service;

import com.dating.ai.dto.UserDTO;
import com.dating.ai.dto.UserLimitsDTO;
import com.dating.ai.dto.UserUsageStatsDTO;

/**
 * Service interface for user-related operations
 *
 * @author dating-ai
 */
public interface UserService {

    /**
     * Get user profile by current authenticated user
     *
     * @return User DTO
     */
    UserDTO getUserProfile();

    /**
     * Update user profile
     *
     * @param userDTO User information to update
     * @return Updated user DTO
     */
    UserDTO updateUserProfile(UserDTO userDTO);

    /**
     * Update user settings
     *
     * @param settings User settings
     * @return Updated user settings
     */
    UserDTO.SettingsDTO updateUserSettings(UserDTO.SettingsDTO settings);

    /**
     * Get user usage statistics
     *
     * @param period Period for statistics (day, week, month, all)
     * @return User usage statistics
     */
    UserUsageStatsDTO getUserUsageStats(String period);

    /**
     * Get user limits based on membership
     *
     * @return User limits
     */
    UserLimitsDTO getUserLimits();
}