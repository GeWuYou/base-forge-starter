package com.gewuyou.web.handler;


import com.gewuyou.core.exception.GlobalException;
import com.gewuyou.core.exception.InternalException;
import com.gewuyou.i18n.enums.ResponseInformation;
import com.gewuyou.util.SpringUtil;
import com.gewuyou.web.entity.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.Objects;

/**
 * 全局异常处理类
 *
 * @author gewuyou
 * @since 2024-04-13 上午12:22:18
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 异常处理器
     *
     * @param e 异常
     * @return com.gewuyou.blog.common.entity.Result<java.lang.Void>
     * @apiNote
     * @since 2024/4/13 上午12:29
     */
    @ExceptionHandler(Exception.class)
    public Result<String> handleException(Exception e) {
        log.error(e.getMessage(), e);
        return Result.failure(HttpStatus.BAD_REQUEST.value(), e.getMessage());
    }

    /**
     * Validated参数异常处理
     */
    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    ResponseEntity<Result<String>> handleBindException(BindException e) {
        log.warn("BindException:", e);
        try {
            // 拿到@NotNull,@NotBlank和 @NotEmpty等注解上的message值
            String msg = Objects.requireNonNull(e.getBindingResult().getFieldError()).getDefaultMessage();
            if (msg != null && msg.isEmpty()) {
                // 自定义状态返回
                return ResponseEntity.status(200).body(Result.failure(HttpStatus.BAD_REQUEST.value(), msg));
            }
        } catch (Exception ignored) {
            log.warn("已忽视解析错误!");
        }
        // 参数类型不匹配检验
        StringBuilder msg = new StringBuilder();
        List<FieldError> fieldErrors = e.getFieldErrors();
        fieldErrors.forEach(oe ->
                {
                    msg.append("参数:[")
                            .append(oe.getObjectName())
                            .append(".")
                            .append(oe.getField())
                            .append("]的传入值:[")
                            .append(oe.getRejectedValue())
                            .append("]与预期的字段类型不匹配.");
                    log.warn(msg.toString());
                    msg.setLength(0);
                    msg.append(oe.getDefaultMessage());
                }
        );
        return ResponseEntity
                .status(200)
                .body(Result.failure(HttpStatus.BAD_REQUEST.value(), msg.toString()));
    }

    /**
     * 全局异常处理器
     *
     * @param e 异常
     * @return com.gewuyou.blog.common.entity.Result<java.lang.Void>
     * @apiNote
     * @since 2024/4/13 下午1:56
     */
    @ExceptionHandler(GlobalException.class)
    public Result<String> handleGlobalException(GlobalException e) {
        return Result.failure(e.getErrorCode(), e.getMessage());
    }

    /**
     * 内部异常处理器
     * @param e 异常
     * @return com.gewuyou.blog.common.entity.Result<java.lang.String>
     */
    @ExceptionHandler(InternalException.class)
    public Result<String> handleGlobalException(InternalException e) {
        return Result.failure(ResponseInformation.INNER_ERROR, SpringUtil.getBean(MessageSource.class));
    }


}
