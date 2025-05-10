package com.dating.ai.dto;

import com.dating.ai.constant.ErrorCode;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用响应格式
 * 所有API端点的统一响应格式
 *
 * @author dating-ai
 * @param <T> 返回数据的类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "通用API响应格式")
public class CommonResponse<T> {

    @Schema(description = "响应码，000000表示成功")
    private String code;

    @Schema(description = "响应消息")
    private String msg;

    @Schema(description = "响应数据")
    private T data;

    /**
     * 成功响应（带数据）
     *
     * @param data 响应数据
     * @param <T>  数据类型
     * @return 包含成功码和数据的CommonResponse对象
     */
    public static <T> CommonResponse<T> success(T data) {
        return new CommonResponse<>(ErrorCode.Common.SUCCESS, null, data);
    }

    /**
     * 成功响应（不带数据）
     *
     * @return 包含成功码的CommonResponse对象
     */
    public static <T> CommonResponse<T> success() {
        return new CommonResponse<>(ErrorCode.Common.SUCCESS, null, null);
    }

    /**
     * 错误响应（自定义错误码和消息）
     *
     * @param code    错误码
     * @param message 错误消息
     * @return 包含错误码和消息的CommonResponse对象
     */
    public static <T> CommonResponse<T> error(String code, String message) {
        return new CommonResponse<>(code, message, null);
    }

    /**
     * 系统错误响应
     *
     * @param message 错误消息
     * @return 包含系统错误码和消息的CommonResponse对象
     */
    public static <T> CommonResponse<T> systemError(String message) {
        return new CommonResponse<>(ErrorCode.Common.INTERNAL_ERROR, message != null ? message : "服务端异常，请稍候重试", null);
    }

    /**
     * 参数错误响应
     *
     * @param message 错误消息
     * @return 包含参数错误码和消息的CommonResponse对象
     */
    public static <T> CommonResponse<T> paramError(String message) {
        return new CommonResponse<>(ErrorCode.Common.PARAM_ERROR, message, null);
    }

    /**
     * 未授权错误响应
     *
     * @param message 错误消息
     * @return 包含未授权错误码和消息的CommonResponse对象
     */
    public static <T> CommonResponse<T> unauthorized(String message) {
        return new CommonResponse<>(ErrorCode.Common.UNAUTHORIZED, message != null ? message : "未经授权，请先登录", null);
    }

    /**
     * 资源不存在错误响应
     *
     * @param message 错误消息
     * @return 包含资源不存在错误码和消息的CommonResponse对象
     */
    public static <T> CommonResponse<T> notFound(String message) {
        return new CommonResponse<>(ErrorCode.Common.NOT_FOUND, message, null);
    }

    /**
     * 权限不足错误响应
     *
     * @param message 错误消息
     * @return 包含权限不足错误码和消息的CommonResponse对象
     */
    public static <T> CommonResponse<T> forbidden(String message) {
        return new CommonResponse<>(ErrorCode.Common.FORBIDDEN, message != null ? message : "权限不足，无法访问", null);
    }
}