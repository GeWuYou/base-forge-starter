package com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * 安全性身份验证属性
 *
 * @author gewuyou
 * @since 2025-01-03 10:12:23
 */
@ConfigurationProperties("base-forge.security.authentication")
data class SecurityAuthenticationProperties(
    /**
     * 登录基础地址
     */
    var baseLoginUrl: String = "/auth/login",

    /**
     * 普通登录页面地址(用户唯一标识符：密码登录)
     */
    var normalLoginUrl: String = "/normal",

    /**
     * 登出页面地址
     */
    var logoutUrl: String = "/logout",
)
