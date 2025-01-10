package com.gewuyou.baseforge.security.authentication.autoconfigure.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.extension.getAccessToken
import com.gewuyou.baseforge.core.extension.getJsonBody
import com.gewuyou.baseforge.entities.web.entity.Result
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.JwtAuthenticationService
import com.gewuyou.baseforge.security.authentication.entities.entity.request.LogoutRequest
import com.gewuyou.baseforge.security.authentication.entities.i18n.enums.SecurityAuthenticationResponseInformation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

/**
 *注销成功处理程序
 *
 * @since 2025-01-03 17:12:37
 * @author gewuyou
 */
class LogoutSuccessJsonResponseHandler(
    private val objectMapper: ObjectMapper,
    private val jwtAuthenticationService: JwtAuthenticationService,
    private val i18nMessageSource: MessageSource
) :LogoutSuccessHandler{
    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // 获取请求体中的数据
        val jsonBody = request.getJsonBody()
        // 获取用户的唯一标识和设备唯一标识
        val requestData = objectMapper.readValue(jsonBody, LogoutRequest::class.java)
        // 获取用户的访问令牌
        val accessToken = request.getAccessToken()?: run {
            throw IllegalArgumentException("Access token is missing")
        }
        // 将携带的访问令牌加入黑名单
        jwtAuthenticationService.addAccessTokenToBlacklist(requestData.principal,requestData.deviceId,accessToken)
        // 从缓存中移除用户的刷新令牌
        jwtAuthenticationService.removeRefreshToken(requestData.principal,requestData.deviceId,requestData.refreshToken)
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val writer = response.writer
        writer.print(
            objectMapper.writeValueAsString(
                Result.success<Any>(SecurityAuthenticationResponseInformation.LOGIN_SUCCESS,i18nMessageSource)
            )
        )
        writer.flush()
        writer.close()
    }
}