package com.dating.ai.config;

import com.dating.ai.interceptor.AuthInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * WebMVC配置类
 * 用于配置拦截器、跨域等Web相关设置
 *
 * @author dating-ai
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final AuthInterceptor authInterceptor;

    /**
     * 添加拦截器
     * 配置需要进行认证拦截的路径和排除的路径
     *
     * @param registry 拦截器注册表
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/api/**") // 拦截所有API请求
                .excludePathPatterns( // 排除不需要认证的路径
                        "/api/auth/login", // 登录接口
                        "/api/auth/refresh", // 刷新令牌接口
                        "/swagger-ui/**", // Swagger UI
                        "/v3/api-docs/**", // OpenAPI规范
                        "/doc.html" // Knife4j文档
                );
    }
}