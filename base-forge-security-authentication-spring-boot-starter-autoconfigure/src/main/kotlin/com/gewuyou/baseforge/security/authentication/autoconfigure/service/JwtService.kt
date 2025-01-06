package com.gewuyou.baseforge.security.authentication.autoconfigure.service

/**
 * jwt 服务
 *
 * @author gewuyou
 * @since 2024-12-31 16:55:47
 */
interface JwtService {
    /**
     * 生成访问token
     *
     * @param userDetails 用户信息Json字符串
     * @param otherClaims 其他声明
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    fun generateToken(userDetails: String, otherClaims: Map<String, Any>?): String

    /**
     * 生成访问token
     *
     * @param userDetails 用户信息对象
     * @param otherClaims 其他声明
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    fun generateToken(userDetails: Any, otherClaims: Map<String, Any>?): String

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
