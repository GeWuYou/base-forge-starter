package com.gewuyou.baseforge.security.authentication.autoconfigure.provider

import java.util.*

/**
 *JWT 令牌提供程序
 *
 * @since 2025-02-01 10:58:55
 * @author gewuyou
 */
interface JwtTokenProvider {
    /**
     * 生成访问token
     *
     * @param principal   用户身份标识
     * @param deviceId    设备标识
     * @param otherClaims 其他声明
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    fun generateToken(principal: String, deviceId: String ,otherClaims: Map<String, Any>?): String

    /**
     * 根据刷新token生成访问token
     * @param refreshToken 刷新token
     * @param otherClaims 其他声明
     * @return java.lang.String 访问token 如果刷新token无效则返回null
     */
    fun generateTokenByRefreshToken(refreshToken: String, principal: String, deviceId: String, otherClaims: Map<String, Any>?): String?


    /**
     * 生成刷新token
     *
     * @return java.lang.String 刷新令牌
     * @since 2023/7/2 19:45
     */
    fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }
}