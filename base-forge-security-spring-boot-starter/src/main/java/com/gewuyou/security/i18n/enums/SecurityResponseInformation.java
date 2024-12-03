package com.gewuyou.security.i18n.enums;

import com.gewuyou.i18n.entity.ResponseInformation;
import org.springframework.http.HttpStatus;

/**
 * 安全响应信息
 *
 * @author gewuyou
 * @since 2024-11-27 23:47:21
 */
public enum SecurityResponseInformation implements ResponseInformation {
    /**
     * 登录已过期
     */
    LoginHasExpired(HttpStatus.UNAUTHORIZED.value(), "security.response.login.expired"),
    /**
     * 登录已失效
     */
    LOGIN_IS_INVALID(HttpStatus.PAYMENT_REQUIRED.value(), "security.response.login.invalid"),
    /**
     * 账户已禁用
     */
    ACCOUNT_IS_DISABLED(HttpStatus.FORBIDDEN.value(), "security.response.account.disabled"),

    /**
     * 账户已过期
     */
    ACCOUNT_HAS_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "security.response.account.expired"),

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
     * 权限不足
     */
    PERMISSION_DENIED(HttpStatus.FORBIDDEN.value(), "security.response.permission.denied"),
    /**
     * 未知错误
     */
    UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR.value(), "security.response.unknown.error"),
    /**
     * 密码未提供
     */
    PASSWORD_NOT_PROVIDED(HttpStatus.BAD_REQUEST.value(), "security.response.password.not.provided"),
    /**
     * 用户名或密码错误
     */
    PASSWORD_NOT_MATCH(HttpStatus.BAD_REQUEST.value(), "security.response.password.not.match"),
    /**
     * 重复的登录请求
     */
    DUPLICATE_SIGN_IN_REQUESTS(HttpStatus.BAD_REQUEST.value(), "security.response.login.repeat"),
    /**
     * 认证已过期
     */
    CERTIFICATION_HAS_EXPIRED(HttpStatus.UNAUTHORIZED.value(), "security.response.certificate.expired"),
    ;

    private final int ResponseCode;
    private final String ResponseI18nMessageCode;

    SecurityResponseInformation(int responseCode, String responseI18nMessageCode) {
        ResponseCode = responseCode;
        ResponseI18nMessageCode = responseI18nMessageCode;
    }

    @Override
    public int getResponseCode() {
        return this.ResponseCode;
    }

    @Override
    public String getResponseI8nMessageCode() {
        return this.ResponseI18nMessageCode;
    }
}
