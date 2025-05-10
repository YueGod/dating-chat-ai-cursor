package com.dating.ai.config;

import com.dating.ai.constant.ErrorCode;
import com.dating.ai.dto.CommonResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 全局异常处理器（旧版本）
 * 已被com.dating.ai.exception.GlobalExceptionHandler替代，请使用新版本
 * 该类将在未来版本中移除
 *
 * @author dating-ai
 * @deprecated 请使用com.dating.ai.exception.GlobalExceptionHandler
 */
@Slf4j
@RestControllerAdvice
@Deprecated
public class GlobalExceptionHandler {

    /**
     * 处理参数验证异常
     * 
     * @param ex 参数验证异常
     * @return 标准API响应
     * @deprecated 请使用com.dating.ai.exception.GlobalExceptionHandler
     */
    @Deprecated
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public CommonResponse<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String errorMessage = String.join("; ", errors);
        log.warn("参数验证错误: {}", errorMessage);
        return CommonResponse.paramError(errorMessage);
    }

    /**
     * 处理绑定异常
     * 
     * @param ex 绑定异常
     * @return 标准API响应
     * @deprecated 请使用com.dating.ai.exception.GlobalExceptionHandler
     */
    @Deprecated
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public CommonResponse<?> handleBindExceptions(BindException ex) {
        List<String> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        String errorMessage = String.join("; ", errors);
        log.warn("绑定错误: {}", errorMessage);
        return CommonResponse.paramError(errorMessage);
    }

    /**
     * 处理所有其他异常
     * 
     * @param ex 异常
     * @return 标准API响应
     * @deprecated 请使用com.dating.ai.exception.GlobalExceptionHandler
     */
    @Deprecated
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler(Exception.class)
    public CommonResponse<?> handleAllExceptions(Exception ex) {
        log.error("发生意外错误", ex);
        return CommonResponse.systemError(ex.getMessage());
    }
}