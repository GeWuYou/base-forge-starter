package com.gewuyou.baseforge.security.authorization.entities.i18n.enums

import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation
import org.springframework.http.HttpStatus

/**
 *安全授权响应信息
 *
 * @since 2025-01-06 15:34:43
 * @author gewuyou
 */
enum class SecurityAuthorizationResponseInformation(
    private val responseCode: Int,
    private val responseI18nMessageCode: String
) : ResponseInformation {
    /**
     * 未提供访问令牌
     */
    MISSING_ACCESS_TOKENS(HttpStatus.UNAUTHORIZED.value(), "security.authorization.response.missing.access.tokens"),

    /**
     * 访问令牌已过期
     */
    ACCESS_TOKEN_HAS_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "security.authorization.response.access.tokens.expired"),

    /**
     * 登录已过期
     */
    LoginHasExpired(HttpStatus.UNAUTHORIZED.value(), "security.response.login.expired"),

    /**
     * 无权限访问
     */
    PROHIBITION_OF_ACCESS(HttpStatus.FORBIDDEN.value(), "security.response.forbidden"),

    /**
     * 用户信息不存在
     */
    USER_DETAILS_NOT_FOUND(HttpStatus.INTERNAL_SERVER_ERROR.value(), "security.response.user.details.not.found"),
    ;
    /**
     * 获取响应码
     * @return 响应码
     */
    override fun getResponseCode(): Int {
        return this.responseCode
    }

    /**
     * 获取i18n响应信息code
     * @return 响应信息 code
     */
    override fun getResponseI8nMessageCode(): String {
        return this.responseI18nMessageCode
    }
}