package com.gewuyou.baseforge.jwt.autoconfigure.service

import com.gewuyou.baseforge.jwt.entities.entity.JwtPayloadData

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
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param otherClaims 其他声明
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    fun generateToken(principal: String, deviceId: String, otherClaims: Map<String, Any>?): String


    /**
     * 根据刷新token生成访问token
     * @param refreshToken 刷新token
     * @param userDetails 用户信息对象
     * @param otherClaims 其他声明
     * @return java.lang.String 访问token 如果刷新token无效则返回null
     */
    fun generateTokenByRefreshToken(refreshToken: String, principal: String, deviceId: String, otherClaims: Map<String, Any>?): String?

    /**
     * 生成刷新token
     *
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识 用于判断登录请求是否重复
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
     * 检查访问token是否在黑名单中
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param token     访问token
     */
    fun checkAccessTokenHasBlacklist(principal: String, deviceId: String, token: String): Boolean

    /**
     * 移除刷新token
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     */
    fun removeRefreshToken(principal: String, deviceId: String, token: String)


    /**
     * 刷新token续期
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param refreshToken 刷新token
     */
    fun refreshTokenRenewal(principal: String, deviceId: String, refreshToken: String)

    /**
     * 验证token`
     * @param token 访问token
     * @return JwtPayloadData 有效载荷数据 如果token无效则返回null
     */
    fun validateToken(token: String): JwtPayloadData?
}
