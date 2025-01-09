package com.gewuyou.baseforge.jwt.autoconfigure.service.impl

import com.gewuyou.baseforge.jwt.autoconfigure.config.entity.JwtProperties
import com.gewuyou.baseforge.jwt.autoconfigure.handler.JwtHandler
import com.gewuyou.baseforge.jwt.autoconfigure.service.JwtService
import com.gewuyou.baseforge.jwt.entities.constants.JwtConstant
import com.gewuyou.baseforge.jwt.entities.entity.JwtPayloadData
import com.gewuyou.baseforge.redis.service.CacheService

/**
 * 默认jwt服务实现
 *
 * @since 2024-12-31 16:56:33
 * @author gewuyou
 */
class DefaultJwtService(
    private val cacheService: CacheService,
    private val jwtHandler: JwtHandler,
    private val jwtProperties: JwtProperties
) :
    JwtService {
    /**
     * 生成访问token
     *
     * @param userDetails 用户信息Json字符串
     * @param otherClaims 其他声明
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    override fun generateToken(principal: String, deviceId: String, otherClaims: Map<String, Any>?): String {
        return jwtHandler.generateToken(principal, deviceId, otherClaims)
    }

    /**
     * 根据刷新token生成访问token
     * @param refreshToken 刷新token
     * @param userDetails 用户信息对象
     * @param otherClaims 其他声明
     * @return java.lang.String 访问token 如果刷新token无效则返回null
     */
    override fun generateTokenByRefreshToken(
        refreshToken: String,
        principal: String,
        deviceId: String,
        otherClaims: Map<String, Any>?
    ): String? {
        // 检查是否存在刷新令牌
        // 生成key
        val key = JwtConstant.REFRESH_TOKEN_CACHE_PREFIX + principal + ":" + deviceId
        cacheService[key]?.let {
            // 生成key
            return generateToken(principal, deviceId, otherClaims)
        } ?: return null
    }

    /**
     * 生成刷新token
     *
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识 用于判断登录请求是否重复
     * @return java.lang.String 刷新令牌
     * @since 2023/7/2 19:45
     */
    override fun generateRefreshToken(principal: String, deviceId: String): String {
        val refreshToken = jwtHandler.generateRefreshToken()
        // 生成key
        val key = JwtConstant.REFRESH_TOKEN_CACHE_PREFIX + principal + ":" + deviceId
        // 缓存刷新token
        cacheService[key, refreshToken] = jwtProperties.refreshTokenExpiration
        return refreshToken
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
     * 检查访问token是否在黑名单中
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param token     访问token
     */
    override fun checkAccessTokenHasBlacklist(principal: String, deviceId: String, token: String): Boolean {
        // 构建key
        val key = JwtConstant.BLACKLIST_ACCESS_TOKEN_CACHE_PREFIX + principal + ":" + deviceId
        // 获取缓存
        return token == cacheService[key]
    }

    /**
     * 移除刷新token
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param token     刷新token
     */
    override fun removeRefreshToken(principal: String, deviceId: String, token: String) {
        // 生成key
        val key = JwtConstant.REFRESH_TOKEN_CACHE_PREFIX + principal + ":" + deviceId
        // 删除缓存的刷新令牌
        cacheService.delete(key)
    }

    /**
     * 刷新token续期
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param refreshToken 刷新token
     */
    override fun refreshTokenRenewal(principal: String, deviceId: String, refreshToken: String) {
        // 生成key
        val key = JwtConstant.REFRESH_TOKEN_CACHE_PREFIX + principal + ":" + deviceId
        // 缓存刷新token
        cacheService[key, refreshToken] = jwtProperties.refreshTokenExpiration
    }

    /**
     * 验证token`
     * @param token 访问token
     * @return JwtPayloadData 有效载荷数据
     */
    override fun validateToken(token: String): JwtPayloadData? {
        return jwtHandler.validateTokenAndGetClaims(token)
    }
}
