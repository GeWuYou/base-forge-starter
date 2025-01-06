package com.gewuyou.baseforge.security.authentication.entities.entity.request

/**
 *注销请求
 *
 * @since 2025-01-03 17:20:57
 * @author gewuyou
 */
data class LogoutRequest(
    /**
     * 用户身份标识
     */
    val principal: String,
    /**
     * 设备标识
     */
    val deviceId: String,
    /**
     * 刷新令牌
     */
    val refreshToken: String
)