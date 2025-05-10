package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录响应DTO
 * 用于返回登录成功后的用户令牌和信息
 *
 * @author dating-ai
 */
@Data
@Schema(description = "登录响应")
public class LoginResponseDTO {

    /**
     * 访问令牌
     */
    @Schema(description = "JWT访问令牌")
    private String token;

    /**
     * 刷新令牌
     */
    @Schema(description = "JWT刷新令牌")
    private String refreshToken;

    /**
     * 令牌有效期（秒）
     */
    @Schema(description = "令牌有效期（秒）", example = "3600")
    private Integer expiresIn;

    /**
     * 用户ID
     */
    @Schema(description = "用户ID")
    private String userId;

    /**
     * 是否新用户
     */
    @Schema(description = "是否新用户", example = "false")
    private Boolean newUser;
}