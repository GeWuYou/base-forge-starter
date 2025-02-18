package com.gewuyou.baseforge.security.authentication.entities.entity.request

/**
 *登录请求
 *
 * @since 2025-02-15 02:13:16
 * @author gewuyou
 */
fun interface LoginRequest {
    fun getLoginType(): String
}