package com.gewuyou.baseforge.security.authentication.autoconfigure.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.entities.web.entity.Result
import com.gewuyou.baseforge.security.authentication.entities.i18n.enums.SecurityAuthenticationResponseInformation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
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
    private val objectMapper: ObjectMapper
) : AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val writer = response.writer
        writer.print(objectMapper.writeValueAsString(Result.failure<String>(SecurityAuthenticationResponseInformation.LoginFailed)))
        writer.flush()
        writer.close()
    }
}