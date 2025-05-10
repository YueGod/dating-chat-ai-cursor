package com.dating.ai.exception;

import lombok.Getter;

/**
 * 业务异常类
 * 用于表示业务逻辑处理过程中的异常情况
 *
 * @author dating-ai
 */
@Getter
public class BusinessException extends RuntimeException {

    /**
     * 错误代码
     */
    private final String errorCode;

    /**
     * 错误消息
     */
    private final String errorMessage;

    /**
     * 构造函数
     *
     * @param errorCode    错误代码
     * @param errorMessage 错误消息
     */
    public BusinessException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}