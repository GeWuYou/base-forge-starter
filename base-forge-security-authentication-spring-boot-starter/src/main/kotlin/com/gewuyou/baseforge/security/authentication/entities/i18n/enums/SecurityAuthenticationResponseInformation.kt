package com.gewuyou.baseforge.security.authentication.entities.i18n.enums

import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation
import org.springframework.http.HttpStatus


/**
 * 安全认证响应信息
 *
 * @author gewuyou
 * @since 2024-11-27 23:47:21
 */
enum class SecurityAuthenticationResponseInformation(
    private val responseCode: Int,
    private val responseI18nMessageCode: String
) :
    ResponseInformation {
    /**
     * 登录成功
     */
    LOGIN_SUCCESS(HttpStatus.OK.value(), "security.response.login.success"),

    /**
     * 账户已禁用
     */
    ACCOUNT_IS_DISABLED(HttpStatus.FORBIDDEN.value(), "security.response.account.disabled"),

    /**
     * 账户凭证已过期
     */
    ACCOUNT_CREDENTIALS_HAVE_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "security.response.credentials.expired"),

    /**
     * 账户已锁定
     */
    ACCOUNT_IS_LOCKED(HttpStatus.LOCKED.value(), "security.response.account.locked"),

    /**
     * 登录失败
     */
    LoginFailed(HttpStatus.UNAUTHORIZED.value(), "security.response.login.failed"),

    /**
     * 密码未提供
     */
    PASSWORD_NOT_PROVIDED(HttpStatus.BAD_REQUEST.value(), "security.response.password.not.provided"),

    /**
     * 用户标识未提供
     */
    PRINCIPAL_NOT_PROVIDED(HttpStatus.BAD_REQUEST.value(), "security.response.principal.not.provided"),

    /**
     * 用户名或密码错误
     */
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "security.response.password.not.match"),

    /**
     * 重复的登录请求
     */
    DUPLICATE_SIGN_IN_REQUESTS(HttpStatus.BAD_REQUEST.value(), "security.response.login.repeat"),

    /**
     * 内部错误
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "invalid.server.error"),

    /**
     * 用户不存在
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND.value(), "security.response.user.not.found"),

    /**
     * 密码已被泄露
     */
    PASSWORD_COMPROMISED(HttpStatus.UNAUTHORIZED.value(), "security.response.password.compromised"),
    ;

    override fun getResponseCode(): Int {
        return this.responseCode
    }

    override fun getResponseI8nMessageCode(): String {
        return this.responseI18nMessageCode
    }
}
