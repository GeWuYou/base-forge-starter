package com.gewuyou.baseforge.security.authentication.autoconfigure.provider

import com.gewuyou.baseforge.jwt.entities.constants.JwtConstant
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.JwtProperties
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

/**
 *默认 JWT 令牌提供程序
 *
 * @since 2025-02-01 11:00:09
 * @author gewuyou
 */
class DefaultJwtTokenProvider(
    private val jwtProperties: JwtProperties,
    private val cacheService: CacheService
): JwtTokenProvider {
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
    override fun generateToken(principal: String, deviceId: String, otherClaims: Map<String, Any>?): String {
        val claims: MutableMap<String, Any> = mutableMapOf(
            JwtConstant.DEVICE_ID to deviceId
        )
        otherClaims?.let { claims.putAll(it) }
        // 创建token
        return Jwts.builder()
            .claims()
            .add(claims)
            // 设置用户唯一标识 通常为用户ID
            .subject(principal)
            // jti (JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
            .id(UUID.randomUUID().toString())
            // 颁发时间
            .issuedAt(Date())
            // 设置颁发者
            .issuer(jwtProperties.issuer)
            // 设置token有效期
            .expiration(Date(System.currentTimeMillis() + jwtProperties.expiration))
            .and()
            .signWith(getKey(jwtProperties.secretKey))
            .compact()
    }

    /**
     * 根据刷新token生成访问token
     * @param refreshToken 刷新token
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

    companion object {
        /**
         * 根据定义的密钥生成加密的key
         *
         * @param secretKey 密钥
         * @return java.security.Key
         * @apiNote
         * @since 2023/7/2 19:53
         */
        private fun getKey(secretKey: String): SecretKey {
            return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey))
        }
    }
}