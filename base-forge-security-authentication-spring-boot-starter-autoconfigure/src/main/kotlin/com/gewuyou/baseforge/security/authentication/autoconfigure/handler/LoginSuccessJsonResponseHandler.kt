package com.gewuyou.baseforge.security.authentication.autoconfigure.handler

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.constants.SecurityAuthenticationCommonConstant
import com.gewuyou.baseforge.core.extension.getDeviceId
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.entities.web.entity.Result
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.JwtAuthenticationService
import com.gewuyou.baseforge.security.authentication.entities.entity.UserDetails
import com.gewuyou.baseforge.security.authentication.entities.exception.AuthenticationException
import com.gewuyou.baseforge.security.authentication.entities.i18n.enums.SecurityAuthenticationResponseInformation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import java.io.IOException
import java.util.*

/**
 * 登录成功Json响应处理程序
 *
 * @author gewuyou
 * @since 2024-11-24 16:16:12
 */
class LoginSuccessJsonResponseHandler(
    private val objectMapper: ObjectMapper,
    private val jwtAuthenticationService: JwtAuthenticationService,
    private val i18nMessageSource: MessageSource
) :
    AbstractAuthenticationTargetUrlRequestHandler(), AuthenticationSuccessHandler {
    @Throws(IOException::class)
    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        // 获取用户详情信息 这里拿details也行
        val userDetails = authentication.principal as UserDetails
        // 获取用户唯一标识
        val principal = userDetails.getUserOnlyIdentity().toString()
        // 尝试获取设备Id
        val deviceId = request.getDeviceId() ?: run {
            log.error("获取设备Id失败!")
            throw AuthenticationException(SecurityAuthenticationResponseInformation.INTERNAL_SERVER_ERROR)
        }
        // 生成access token 这里userDetails使用Authentication.principal也行
        val accessToken =
            jwtAuthenticationService
                .generateToken(principal, deviceId, mapOf(SecurityAuthenticationCommonConstant.USER_DETAILS to userDetails))
        // 生成刷新token
        val refreshToken = jwtAuthenticationService.generateRefreshToken(principal, deviceId)
        // 生成返回结果
        val result = mapOf(
            "access_token" to accessToken,
            "refresh_token" to refreshToken,
            "details" to userDetails
        )
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val writer = response.writer
        writer.print(
            objectMapper.writeValueAsString(
                Result.success(
                    SecurityAuthenticationResponseInformation.LOGIN_SUCCESS,
                    result,
                    i18nMessageSource
                )
            )
        )
        writer.flush()
        writer.close()
    }
}
