package com.gewuyou.baseforge.security.authentication.entities.entity.request

/**
 * 普通登录请求实体
 *
 * @author gewuyou
 * @since 2025-01-01 23:23:23
 */
data class NormalLoginRequest(
    /**
     * 用户登录传递的信息(通常是用户名 邮箱 手机号)
     */
    val principal: String,

    /**
     * 用户登录传递的凭证(密码 验证码)
     */
    val credentials: String
)