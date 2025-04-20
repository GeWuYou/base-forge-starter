package com.gewuyou.baseforge.security.authentication.autoconfigure.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.entities.web.entity.Result
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

/**
 *身份验证异常处理程序
 *
 * @since 2025-01-03 14:50:46
 * @author gewuyou
 */
class AuthenticationExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val i18nMessageSource: MessageSource
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        log.error("身份验证异常: ", authException)
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val writer = response.writer
        writer.print(objectMapper.writeValueAsString(Result.failure<String>(authException.message,i18nMessageSource)))
        writer.flush()
        writer.close()
    }
}