package com.dating.ai.controller;

import com.dating.ai.dto.CommonResponse;
import com.dating.ai.dto.LoginRequestDTO;
import com.dating.ai.dto.RefreshTokenRequestDTO;
import com.dating.ai.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 认证控制器
 * 提供登录、刷新令牌等认证相关的API端点
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "认证API", description = "登录、刷新令牌等认证相关接口")
public class AuthController {

    private final AuthService authService;

    /**
     * 登录接口
     * 通过微信授权code登录系统
     *
     * @param loginRequest 登录请求
     * @return 登录结果，包含访问令牌等信息
     */
    @PostMapping("/login")
    @Operation(summary = "微信登录", description = "通过微信授权code登录系统")
    public CommonResponse<?> login(@RequestBody LoginRequestDTO loginRequest) {
        return CommonResponse.success(authService.login(loginRequest));
    }

    /**
     * 刷新令牌接口
     * 使用刷新令牌获取新的访问令牌
     *
     * @param refreshRequest 刷新请求
     * @return 刷新结果，包含新的访问令牌
     */
    @PostMapping("/refresh")
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    public CommonResponse<?> refreshToken(@RequestBody RefreshTokenRequestDTO refreshRequest) {
        return CommonResponse.success(authService.refreshToken(refreshRequest.getRefreshToken()));
    }

    /**
     * 退出登录接口
     * 使当前用户的令牌失效
     *
     * @return 退出登录结果
     */
    @PostMapping("/logout")
    @Operation(summary = "退出登录", description = "使当前用户的令牌失效")
    public CommonResponse<?> logout() {
        return CommonResponse.success(authService.logout());
    }
}