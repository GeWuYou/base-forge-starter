package com.gewuyou.baseforge.security.authentication.autoconfigure.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.entities.web.entity.Result
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import java.io.IOException

/**
 * 登录失败json响应处理程序
 *
 * @author gewuyou
 * @since 2024-11-24 20:09:43
 */
class LoginFailJsonResponseHandler(
    private val objectMapper: ObjectMapper,
    private val i18nMessageSource: MessageSource
) : AuthenticationFailureHandler {
    @Throws(IOException::class)
    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val message = exception.message
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val writer = response.writer
        writer.print(
            objectMapper.writeValueAsString(
                Result.failure<Any>(
                    HttpStatus.INTERNAL_SERVER_ERROR.value(),
                    message,
                    i18nMessageSource
                )
            )
        )
        writer.flush()
        writer.close()
    }
}
