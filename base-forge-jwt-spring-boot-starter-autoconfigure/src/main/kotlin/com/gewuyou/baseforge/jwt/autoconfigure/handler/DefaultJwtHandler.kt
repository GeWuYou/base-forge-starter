package com.gewuyou.baseforge.jwt.autoconfigure.handler

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.jwt.autoconfigure.config.entity.JwtProperties
import com.gewuyou.baseforge.jwt.entities.constants.JwtConstant
import com.gewuyou.baseforge.jwt.entities.entity.JwtPayloadData
import io.jsonwebtoken.Claims
import io.jsonwebtoken.JwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import java.util.*
import javax.crypto.SecretKey

/**
 * 默认 JWT 处理程序
 *
 * @author gewuyou
 * @since 2024-12-31 16:09:51
 */
class DefaultJwtHandler(
    private val jwtProperties: JwtProperties
) :
    JwtHandler {
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
     * 验证访问令牌并获取数据声明
     *
     * @param token 令牌
     * @return java.util.Optional<java.lang.String> 用户信息json字符串
    </java.lang.String> */
    override fun validateTokenAndGetClaims(token: String): JwtPayloadData? {
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
            DefaultJwtHandler.log.error("解析token失败", e)
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
