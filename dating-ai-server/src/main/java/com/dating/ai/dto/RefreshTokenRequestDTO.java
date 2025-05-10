package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 刷新令牌请求DTO
 * 用于接收刷新令牌的请求参数
 *
 * @author dating-ai
 */
@Data
@Schema(description = "刷新令牌请求")
public class RefreshTokenRequestDTO {

    /**
     * 刷新令牌
     */
    @Schema(description = "JWT刷新令牌", required = true)
    private String refreshToken;
}