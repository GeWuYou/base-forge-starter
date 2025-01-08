package com.gewuyou.baseforge.security.authentication.autoconfigure.service

/**
 * JWT 身份验证服务
 *
 * @author gewuyou
 * @since 2024-12-31 16:55:47
 */
interface JwtAuthenticationService {
    /**
     * 生成访问token
     *
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param otherClaims 其他声明
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    fun generateToken(principal: String, deviceId: String, otherClaims: Map<String, Any>?): String

    /**
     * 生成刷新token
     *
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @return java.lang.String 刷新令牌
     * @since 2023/7/2 19:45
     */
    fun generateRefreshToken(principal: String, deviceId: String): String

    /**
     * 将访问token加入黑名单
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     */
    fun addAccessTokenToBlacklist(principal: String, deviceId: String, token: String)

    /**
     * 移除刷新token
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     */
    fun removeRefreshToken(principal: String, deviceId: String, token: String)
}
