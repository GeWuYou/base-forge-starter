package com.gewuyou.baseforge.security.authorization.autoconfigure.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.exception.GlobalException
import com.gewuyou.baseforge.core.exception.InternalException
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.entities.web.entity.Result
import com.gewuyou.baseforge.entities.web.i18n.enums.WebResponseInformation
import com.gewuyou.baseforge.security.authorization.entities.exception.AuthorizationException
import com.gewuyou.baseforge.security.authorization.entities.i18n.enums.SecurityAuthorizationResponseInformation
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

/**
 *反应式全局异常处理程序
 *
 * @since 2025-02-08 00:28:12
 * @author gewuyou
 */
@ControllerAdvice
class ReactiveGlobalExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val i18nMessageSource: MessageSource
) : WebExceptionHandler{
    /**
     * 异常处理器
     *
     * @param e 异常
     * @return 响应
     * @since 2024/4/13 上午12:29
     */
    @ExceptionHandler(Exception::class)
    fun handleException(exchange: ServerWebExchange, e: Exception): Mono<Void> {
        log.error("其它异常:", e)
        // 设置响应状态码为 500 Internal Server Error
        exchange.response.statusCode = HttpStatus.OK
        exchange.response.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        return exchange.response.writeWith(
            Mono.just(
                exchange.response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(
                        Result.failure<String>(WebResponseInformation.INTERNAL_SERVER_ERROR, i18nMessageSource)
                    )
                )
            )
        )
    }

    /**
     * 全局异常处理器
     *
     * @param e 异常
     * @return 返回的结果
     * @since 2024/4/13 下午1:56
     */
    @ExceptionHandler(GlobalException::class)
    fun handleInternalException(exchange: ServerWebExchange, e: GlobalException): Mono<Void> {
        val errorCode = e.errorCode
        exchange.response.statusCode = HttpStatus.valueOf(errorCode)
        exchange.response.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        return exchange.response.writeWith(
            Mono.just(
                exchange.response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(
                        Result.failure<String>(
                            errorCode,
                            e.errorI18nMessageCode,
                            i18nMessageSource
                        )
                    )
                )
            )
        )
    }

    /**
     * 内部异常处理器
     *
     * @param e 异常
     * @return 返回的结果
     */
    @ExceptionHandler(InternalException::class)
    fun handleInternalException(exchange: ServerWebExchange, e: InternalException): Mono<Void> {
        log.error("内部异常: 异常信息: {}", e.errorMessage, e)
        if (e.internalInformation != null) {
            log.error(
                "i18nMessage: {}", i18nMessageSource
                    .getMessage(
                        e
                            .internalInformation
                            .responseI8nMessageCode, null, LocaleContextHolder.getLocale()
                    )
            )
        }
        // 设置响应状态码为 500 Internal Server Error
        exchange.response.statusCode = HttpStatus.INTERNAL_SERVER_ERROR
        exchange.response.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        return exchange.response.writeWith(
            Mono.just(
                exchange.response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(
                        Result.failure<String>(WebResponseInformation.INTERNAL_SERVER_ERROR, i18nMessageSource)
                    )
                )
            )
        )
    }

    // 处理 AuthorizationException 异常
    @ExceptionHandler(AuthorizationException::class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    fun handleAuthorizationException(
        exchange: ServerWebExchange,
        e: AuthorizationException
    ): Mono<Void> {
        log.error("授权异常：${exchange.request.uri}", e)
        // 设置响应状态码为 403 Forbidden
        exchange.response.statusCode = HttpStatus.FORBIDDEN
        exchange.response.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        // 返回自定义的错误信息
        return exchange.response.writeWith(
            Mono.just(
                exchange.response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(
                        Result.failure<String>(
                            SecurityAuthorizationResponseInformation.LoginHasExpired,
                            i18nMessageSource
                        )
                    )
                )
            )
        )
    }

    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
        return when (ex) {
            is AuthorizationException -> handleAuthorizationException(exchange, ex)
            is InternalException -> handleInternalException(exchange, ex)
            is GlobalException -> handleInternalException(exchange, ex)
            else -> handleException(exchange, ex as Exception)
        }
    }
}