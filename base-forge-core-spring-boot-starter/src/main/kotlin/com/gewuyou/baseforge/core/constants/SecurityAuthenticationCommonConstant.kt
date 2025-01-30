package com.gewuyou.baseforge.core.constants

/**
 *安全性 认证 公共常量
 *
 * @since 2025-01-02 16:18:26
 * @author gewuyou
 */
object SecurityAuthenticationCommonConstant {
    /**
     * 幂等性登录请求前缀
     */
    const val IDEMPOTENT_LOGIN_REQUEST_PREFIX: String = "idempotent_login_request_"

    /**
     * 登录成功后，保存用户信息的key
     */
    const val USER_DETAILS = "userDetails"
}