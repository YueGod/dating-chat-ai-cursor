package com.dating.ai.interceptor;

import com.dating.ai.constant.ErrorCode;
import com.dating.ai.exception.BusinessException;
import com.dating.ai.utils.JwtUtils;
import com.dating.ai.utils.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 认证拦截器
 * 用于验证用户请求的JWT令牌
 *
 * @author dating-ai
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class AuthInterceptor implements HandlerInterceptor {

    private final JwtUtils jwtUtils;

    /**
     * 在请求处理之前进行调用
     * 验证JWT令牌并设置用户上下文
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器
     * @return 是否继续处理
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = getTokenFromRequest(request);

        if (!StringUtils.hasText(token) || !jwtUtils.validateToken(token)) {
            throw new BusinessException(ErrorCode.Auth.INVALID_TOKEN, "未授权或令牌已过期");
        }

        // 设置用户上下文
        String userId = jwtUtils.getUserIdFromToken(token);
        UserContext.setUserId(userId);

        return true;
    }

    /**
     * 在请求完成之后，视图渲染之前执行
     * 清除用户上下文
     *
     * @param request  请求对象
     * @param response 响应对象
     * @param handler  处理器
     * @param ex       异常对象
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex) {
        // 清除用户上下文
        UserContext.clear();
    }

    /**
     * 从请求头中提取Bearer令牌
     *
     * @param request 请求对象
     * @return 令牌字符串
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}