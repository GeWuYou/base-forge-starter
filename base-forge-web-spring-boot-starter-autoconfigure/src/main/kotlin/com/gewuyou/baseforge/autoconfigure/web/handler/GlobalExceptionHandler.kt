package com.gewuyou.baseforge.autoconfigure.web.handler

import com.gewuyou.baseforge.core.exception.GlobalException
import com.gewuyou.baseforge.core.exception.InternalException
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.entities.web.entity.Result
import com.gewuyou.baseforge.entities.web.i18n.enums.WebResponseInformation
import jakarta.validation.ConstraintViolationException
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpStatus
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice


/**
 * 全局异常处理类
 *
 * @author gewuyou
 * @since 2024-04-13 上午12:22:18
 */
@RestControllerAdvice
class GlobalExceptionHandler(
    private val i18nMessageSource: MessageSource
) {
    /**
     * 异常处理器
     *
     * @param e 异常
     * @return 响应
     * @since 2024/4/13 上午12:29
     */
    @ExceptionHandler(Exception::class)
    fun handleException(e: Exception): Result<String> {
        log.error("其它异常:", e)
        return Result.failure(WebResponseInformation.INTERNAL_SERVER_ERROR, i18nMessageSource)
    }

    /**
     * 处理 @Valid 和 @Validated 校验失败抛出的 MethodArgumentNotValidException 异常
     *
     * @param ex 异常
     * @return 响应信息
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(ex: MethodArgumentNotValidException): Result<String> {
        // 返回字段错误
        for (fieldError in ex.bindingResult.fieldErrors) {
            return Result.failure(HttpStatus.BAD_REQUEST.value(), fieldError.defaultMessage, i18nMessageSource)
        }
        // 返回全局错误
        for (objectError in ex.bindingResult.globalErrors) {
            return Result.failure(HttpStatus.BAD_REQUEST.value(), objectError.defaultMessage, i18nMessageSource)
        }
        return Result.failure(WebResponseInformation.ILLEGAL_PARAMETERS, i18nMessageSource)
    }

    /**
     * 处理 JSR 303/JSR 380 校验失败抛出的 ConstraintViolationException 异常
     *
     * @param ex 异常
     * @return 响应信息
     */
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException): Result<String> {
        for (constraintViolation in ex.constraintViolations) {
            return Result.failure(HttpStatus.BAD_REQUEST.value(), constraintViolation.message, i18nMessageSource)
        }
        return Result.failure(WebResponseInformation.ILLEGAL_PARAMETERS, i18nMessageSource)
    }

    /**
     * 全局异常处理器
     *
     * @param e 异常
     * @return 返回的结果
     * @since 2024/4/13 下午1:56
     */
    @ExceptionHandler(GlobalException::class)
    fun handleGlobalException(e: GlobalException): Result<String> {
        return Result.failure(e.errorCode, e.errorI18nMessageCode, i18nMessageSource)
    }

    /**
     * 内部异常处理器
     *
     * @param e 异常
     * @return 返回的结果
     */
    @ExceptionHandler(InternalException::class)
    fun handleGlobalException(e: InternalException): Result<String> {
        log.error("内部异常: 异常信息: {}", e.errorMessage, e)
        if(e.internalInformation!= null){
            log.error(
                "i18nMessage: {}", i18nMessageSource
                    .getMessage(
                        e
                            .internalInformation
                            .responseI8nMessageCode, null, LocaleContextHolder.getLocale()
                    )
            )
        }
        return Result.failure(WebResponseInformation.INTERNAL_SERVER_ERROR, i18nMessageSource)
    }
}
