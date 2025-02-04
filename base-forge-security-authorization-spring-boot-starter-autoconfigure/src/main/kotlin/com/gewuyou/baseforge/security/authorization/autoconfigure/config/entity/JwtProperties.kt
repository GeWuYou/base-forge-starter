package com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity

import org.springframework.boot.context.properties.ConfigurationProperties

/**
 * jwt配置
 *
 * @author gewuyou
 * @since 2024-12-31 15:26:52
 */
@ConfigurationProperties(prefix = "base-forge.security.jwt")
data class JwtProperties(
    /**
     * 密钥
     */
    var secretKey: String = "base-forge-security-secret-key",


    /**
     * 访问令牌过期时间 (单位：毫秒, 默认15分钟)
     */
    var expiration: Long = 900000,

    /**
     * 刷新token过期时间 (单位：毫秒, 默认30天)
     */
    var refreshTokenExpiration: Long = 2592000000L,
)