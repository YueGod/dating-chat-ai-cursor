package com.dating.ai.service;

import com.dating.ai.dto.LoginRequestDTO;
import com.dating.ai.dto.LoginResponseDTO;

/**
 * 认证服务接口
 * 提供用户登录、令牌刷新等功能
 *
 * @author dating-ai
 */
public interface AuthService {

    /**
     * 用户登录
     * 通过微信授权码登录系统，返回令牌信息
     *
     * @param loginRequest 登录请求
     * @return 登录响应信息，包含令牌等
     */
    LoginResponseDTO login(LoginRequestDTO loginRequest);

    /**
     * 刷新令牌
     * 通过刷新令牌获取新的访问令牌
     *
     * @param refreshToken 刷新令牌
     * @return 刷新后的令牌信息
     */
    LoginResponseDTO refreshToken(String refreshToken);

    /**
     * 退出登录
     * 使当前用户的令牌失效
     *
     * @return 退出登录结果
     */
    Object logout();
}