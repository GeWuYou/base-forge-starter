package com.gewuyou.baseforge.security.authorization.autoconfigure.validator

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.jwt.entities.constants.JwtConstant
import com.gewuyou.baseforge.jwt.entities.entity.JwtPayloadData
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

/**
 *默认 JWT 验证器
 *
 * @since 2025-02-01 11:15:56
 * @author gewuyou
 */
class DefaultJwtValidator(
    private val jwtProperties: JwtProperties,
    private val cacheService: CacheService
) : JwtValidator {
    /**
     * 验证访问令牌并获取数据声明
     *
     * @param token 令牌
     * @return 用户信息json字符串
     */
    override fun validateTokenAndGetJwtPayloadData(token: String): JwtPayloadData? {
        try {
            val claims = getClaimsFromToken(token)
            return if (isTokenExpired(claims)) {
                null
            } else {
                JwtPayloadData(
                    claims.toMap(),
                    claims.id,
                    claims.subject,
                    claims[JwtConstant.DEVICE_ID] as String
                )
            }
        } catch (e: Exception) {
            return null
        }
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
     * 判断token是否过期
     *
     * @param claims 数据声明
     * @return boolean 未失效
     * @apiNote 过期返回true反之false
     * @since 2023/7/2 20:33
     */
    private fun isTokenExpired(claims: Claims): Boolean {
        return claims.expiration.before(Date())
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token 令牌
     * @return io.jsonwebtoken.Claims
     * @apiNote
     * @since 2023/7/2 20:02
     */
    private fun getClaimsFromToken(token: String): Claims {
        try {
            return Jwts.parser()
                .verifyWith(getKey(jwtProperties.secretKey))
                .build()
                .parseSignedClaims(token)
                .payload
        } catch (e: Exception) {
            log.error("解析token失败", e)
            throw JwtException("解析token失败", e)
        }
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