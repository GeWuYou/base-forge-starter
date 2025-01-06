package com.gewuyou.baseforge.security.authentication.autoconfigure.interceptor

import com.gewuyou.baseforge.core.constants.SecurityAuthenticationCommonConstant
import com.gewuyou.baseforge.core.extension.getDeviceId
import com.gewuyou.baseforge.core.extension.getJsonBody
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authentication.entities.exception.AuthenticationException
import com.gewuyou.baseforge.security.authentication.entities.i18n.enums.SecurityAuthenticationResponseInformation
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.time.Duration

/**
 *幂等登录请求拦截器
 *
 * @since 2025-01-02 15:40:14
 * @author gewuyou
 */
class DefaultIdempotentLoginRequestInterceptor(
    private val cacheService: CacheService
) : IdempotentLoginRequestInterceptor{
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val jsonBody = request.getJsonBody()
        val deviceId = request.getDeviceId()
        val cacheKey = SecurityAuthenticationCommonConstant.IDEMPOTENT_LOGIN_REQUEST_PREFIX + deviceId + jsonBody
        // 尝试设置缓存 1分钟过期保证幂等性
        if (cacheService.setIfAbsent(cacheKey, true, Duration.ofMinutes(1))) {
            return true
        } else {
            throw AuthenticationException(SecurityAuthenticationResponseInformation.DUPLICATE_SIGN_IN_REQUESTS)
        }
    }
}