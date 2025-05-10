package com.dating.ai.exception;

import com.dating.ai.constant.ErrorCode;
import com.dating.ai.dto.CommonResponse;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.stream.Collectors;

/**
 * 全局异常处理器
 * 统一处理系统中的各类异常，并转换为标准的API响应格式
 *
 * @author dating-ai
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 处理业务异常
     * 
     * @param e 业务异常
     * @return 标准API响应
     */
    @ExceptionHandler(BusinessException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> handleBusinessException(BusinessException e) {
        log.warn("业务异常: {}", e.getMessage());
        return CommonResponse.error(e.getErrorCode(), e.getErrorMessage());
    }

    /**
     * 处理请求参数验证异常
     * 
     * @param e 参数验证异常
     * @return 标准API响应
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数验证失败: {}", message);
        return CommonResponse.paramError(message);
    }

    /**
     * 处理Bean验证异常
     * 
     * @param e Bean验证异常
     * @return 标准API响应
     */
    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> handleConstraintViolationException(ConstraintViolationException e) {
        String message = e.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数验证失败: {}", message);
        return CommonResponse.paramError(message);
    }

    /**
     * 处理绑定异常
     * 
     * @param e 绑定异常
     * @return 标准API响应
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> handleBindException(BindException e) {
        String message = e.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.joining(", "));
        log.warn("参数绑定失败: {}", message);
        return CommonResponse.paramError(message);
    }

    /**
     * 处理缺少请求参数异常
     * 
     * @param e 缺少请求参数异常
     * @return 标准API响应
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> handleMissingServletRequestParameterException(MissingServletRequestParameterException e) {
        log.warn("缺少请求参数: {}", e.getMessage());
        return CommonResponse.paramError("缺少必要的请求参数: " + e.getParameterName());
    }

    /**
     * 处理参数类型不匹配异常
     * 
     * @param e 参数类型不匹配异常
     * @return 标准API响应
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.OK)
    public CommonResponse<?> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException e) {
        log.warn("参数类型不匹配: {}", e.getMessage());
        return CommonResponse.paramError("参数类型不匹配: " + e.getName());
    }

    /**
     * 处理所有未捕获的异常
     * 
     * @param e 未捕获的异常
     * @return 标准API响应
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public CommonResponse<?> handleException(Exception e) {
        log.error("未处理的异常", e);
        return CommonResponse.systemError("服务器内部错误，请稍后重试");
    }
}