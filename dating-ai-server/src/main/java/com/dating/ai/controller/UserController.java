package com.dating.ai.controller;

import com.dating.ai.dto.CommonResponse;
import com.dating.ai.dto.UserDTO;
import com.dating.ai.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user-related endpoints
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "User API", description = "User management endpoints")
public class UserController {

    private final UserService userService;

    @GetMapping("/profile")
    @Operation(summary = "Get user profile", description = "Retrieve the user's profile information")
    public CommonResponse<?> getUserProfile() {
        return CommonResponse.success(userService.getUserProfile());
    }

    @PutMapping("/profile")
    @Operation(summary = "Update user profile", description = "Update the user's profile information")
    public CommonResponse<?> updateUserProfile(@RequestBody UserDTO userDTO) {
        return CommonResponse.success(userService.updateUserProfile(userDTO));
    }

    @PutMapping("/settings")
    @Operation(summary = "Update user settings", description = "Update the user's settings")
    public CommonResponse<?> updateUserSettings(@RequestBody UserDTO.SettingsDTO settings) {
        return CommonResponse.success(userService.updateUserSettings(settings));
    }

    @GetMapping("/usage-stats")
    @Operation(summary = "Get user usage statistics", description = "Retrieve the user's usage statistics")
    public CommonResponse<?> getUserUsageStats(
            @Parameter(description = "Period (day, week, month, all)") @RequestParam(required = false, defaultValue = "day") String period) {
        return CommonResponse.success(userService.getUserUsageStats(period));
    }

    @GetMapping("/limits")
    @Operation(summary = "Get user limits", description = "Retrieve the user's usage limits")
    public CommonResponse<?> getUserLimits() {
        return CommonResponse.success(userService.getUserLimits());
    }
}