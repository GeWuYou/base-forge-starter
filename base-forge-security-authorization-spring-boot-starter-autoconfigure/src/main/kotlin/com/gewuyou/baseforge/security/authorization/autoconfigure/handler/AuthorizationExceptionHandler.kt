package com.gewuyou.baseforge.security.authorization.autoconfigure.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.entities.web.entity.Result
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

/**
 *授权异常处理程序
 *
 * @since 2025-01-08 14:12:09
 * @author gewuyou
 */
class AuthorizationExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val i18nMessageSource: MessageSource
) : AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException
    ) {
        response.status = HttpStatus.OK.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val writer = response.writer
        writer.print(
            objectMapper.writeValueAsString(
                Result.failure<String>(
                    accessDeniedException.message,
                    i18nMessageSource
                )
            )
        )
        writer.flush()
        writer.close()
    }
}