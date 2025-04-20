package com.gewuyou.baseforge.security.authorization.autoconfigure.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.entities.web.entity.Result
import com.gewuyou.baseforge.security.authorization.entities.i18n.enums.SecurityAuthorizationResponseInformation
import org.springframework.context.MessageSource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

/**
 *反应式授权异常处理程序
 *
 * @since 2025-02-03 18:21:40
 * @author gewuyou
 */
class ReactiveAuthorizationExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val i18nMessageSource: MessageSource
) : ServerAccessDeniedHandler {
    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        log.error("反应式授权异常 路径：${exchange.request.uri}", denied)
        // 设置响应状态码为 200 OK
        exchange.response.statusCode = HttpStatus.OK
        // 设置响应内容类型为 application/json
        exchange.response.headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        // 将错误响应体写入响应
        return exchange.response.writeWith(
            Mono.just(
                exchange.response.bufferFactory().wrap(
                    objectMapper.writeValueAsBytes(
                        Result.failure<String>(
                            SecurityAuthorizationResponseInformation.PROHIBITION_OF_ACCESS,
                            i18nMessageSource
                        )
                    )
                )
            )
        )
    }
}