package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 登录请求DTO
 * 用于接收微信登录相关的请求参数
 *
 * @author dating-ai
 */
@Data
@Schema(description = "登录请求")
public class LoginRequestDTO {

    /**
     * 微信授权码
     */
    @Schema(description = "微信登录授权code", required = true)
    private String code;

    /**
     * 登录平台
     */
    @Schema(description = "登录平台", example = "WECHAT_MINI_PROGRAM")
    private String platform;
}