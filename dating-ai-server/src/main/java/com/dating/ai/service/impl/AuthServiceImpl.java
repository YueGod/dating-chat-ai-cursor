package com.dating.ai.service.impl;

import com.dating.ai.constant.ErrorCode;
import com.dating.ai.dao.UserRepository;
import com.dating.ai.domain.User;
import com.dating.ai.dto.LoginRequestDTO;
import com.dating.ai.dto.LoginResponseDTO;
import com.dating.ai.exception.BusinessException;
import com.dating.ai.service.AuthService;
import com.dating.ai.utils.JwtUtils;
import com.dating.ai.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 认证服务实现类
 * 实现用户登录、令牌刷新等功能
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    /**
     * JWT令牌有效期（秒）
     */
    @Value("${jwt.expiration}")
    private int jwtExpiration;

    /**
     * 用户登录实现
     * 模拟微信登录，真实场景中需要调用微信API进行登录验证
     * 
     * @param loginRequest 登录请求对象
     * @return 登录响应，包含令牌等信息
     */
    @Override
    @Transactional
    public LoginResponseDTO login(LoginRequestDTO loginRequest) {
        // 验证微信登录授权码
        // 实际实现中需要调用微信API验证code并获取用户openId
        String openId = "mock_" + UUID.randomUUID().toString();

        // 检查用户是否存在，不存在则创建
        User user = userRepository.findByOpenId(openId)
                .orElseGet(() -> createNewUser(openId));

        // 更新最后登录时间
        user.setLastLoginDate(new Date());
        userRepository.save(user);

        // 生成令牌
        String token = jwtUtils.generateToken(user.getUserId());
        String refreshToken = jwtUtils.generateRefreshToken(user.getUserId());

        // 构建登录响应
        LoginResponseDTO loginResponse = new LoginResponseDTO();
        loginResponse.setToken(token);
        loginResponse.setRefreshToken(refreshToken);
        loginResponse.setExpiresIn(jwtExpiration);
        loginResponse.setUserId(user.getUserId());
        loginResponse.setNewUser(user.getCreateTime().equals(user.getUpdateTime()));

        return loginResponse;
    }

    /**
     * 刷新令牌实现
     * 使用刷新令牌获取新的访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 刷新响应，包含新的访问令牌
     */
    @Override
    public LoginResponseDTO refreshToken(String refreshToken) {
        // 验证刷新令牌
        if (!jwtUtils.validateToken(refreshToken)) {
            throw new BusinessException(ErrorCode.Auth.INVALID_REFRESH_TOKEN, "刷新令牌已过期或无效");
        }

        // 从刷新令牌中获取用户ID
        String userId = jwtUtils.getUserIdFromToken(refreshToken);

        // 检查用户是否存在
        userRepository.findByUserId(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.User.USER_NOT_FOUND, "用户不存在"));

        // 生成新的访问令牌
        String newToken = jwtUtils.generateToken(userId);

        // 构建响应
        LoginResponseDTO response = new LoginResponseDTO();
        response.setToken(newToken);
        response.setExpiresIn(jwtExpiration);
        response.setUserId(userId);

        return response;
    }

    /**
     * 退出登录实现
     * 实际实现可以将令牌加入黑名单
     * 
     * @return 退出结果
     */
    @Override
    public Object logout() {
        String userId = UserContext.getUserId();
        log.info("用户退出登录: {}", userId);

        // 在实际实现中，可以将当前令牌加入黑名单
        // 这里简单返回成功信息
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("logoutTime", new Date());

        return result;
    }

    /**
     * 创建新用户
     * 当用户首次登录时调用，创建并保存新用户记录
     *
     * @param openId 微信OpenID
     * @return 新创建的用户
     */
    private User createNewUser(String openId) {
        User newUser = new User();
        newUser.setUserId(UUID.randomUUID().toString());
        newUser.setOpenId(openId);
        newUser.setNickname("用户" + openId.substring(openId.length() - 6));
        newUser.setUserStatus("active");
        newUser.setRegistrationDate(new Date());
        newUser.setLastLoginDate(new Date());

        // 创建默认设置
        User.UserSettings settings = new User.UserSettings();
        settings.setNotification(true);
        settings.setPrivacy("standard");
        settings.setChatHistoryRetention(30);
        settings.setLanguage("zh-CN");
        newUser.setSettings(settings);

        Date now = new Date();
        newUser.setCreateTime(now);
        newUser.setUpdateTime(now);

        return userRepository.save(newUser);
    }
}