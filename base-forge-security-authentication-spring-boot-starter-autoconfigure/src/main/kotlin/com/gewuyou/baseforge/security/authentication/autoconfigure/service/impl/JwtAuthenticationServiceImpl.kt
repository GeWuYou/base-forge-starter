package com.gewuyou.baseforge.security.authentication.autoconfigure.service.impl

import com.gewuyou.baseforge.jwt.entities.constants.JwtConstant
import com.gewuyou.baseforge.security.authentication.autoconfigure.provider.JwtTokenProvider
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.JwtProperties
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.JwtAuthenticationService

/**
 *JWT 身份验证服务 实现
 *
 * @since 2025-02-01 16:06:32
 * @author gewuyou
 */
class JwtAuthenticationServiceImpl(
    private val jwtTokenProvider: JwtTokenProvider,
    private val cacheService: CacheService,
    private val jwtProperties: JwtProperties
) : JwtAuthenticationService {
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
    override fun generateToken(principal: String, deviceId: String, otherClaims: Map<String, Any>?): String {
        return jwtTokenProvider.generateToken(principal, deviceId, otherClaims)
    }

    /**
     * 生成刷新token
     *
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @return java.lang.String 刷新令牌
     * @since 2023/7/2 19:45
     */
    override fun generateRefreshToken(principal: String, deviceId: String): String {
        return jwtTokenProvider.generateRefreshToken()
    }

    /**
     * 将访问token加入黑名单
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     */
    override fun addAccessTokenToBlacklist(principal: String, deviceId: String, token: String) {
        val key = JwtConstant.BLACKLIST_ACCESS_TOKEN_CACHE_PREFIX + principal + ":" + deviceId
        // 设置缓存
        cacheService[key, token] = jwtProperties.expiration
    }

    /**
     * 移除刷新token
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     */
    override fun removeRefreshToken(principal: String, deviceId: String, token: String) {
        // 生成key
        val key = JwtConstant.REFRESH_TOKEN_CACHE_PREFIX + principal + ":" + deviceId
        // 删除缓存的刷新令牌
        cacheService.delete(key)
    }
}