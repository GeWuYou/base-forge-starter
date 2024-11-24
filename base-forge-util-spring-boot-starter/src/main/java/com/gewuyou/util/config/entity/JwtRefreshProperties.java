package com.gewuyou.util.config.entity;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * JWT Refresh Token 配置
 *
 * @author gewuyou
 * @since 2024-11-20 17:30:55
 */
@ConfigurationProperties(prefix = "jwt.refresh")
@Setter
public class JwtRefreshProperties implements IJwtProperties{
    /**
     * 密钥
     */
    private String secretKey;
    /**
     * 颁发者
     */
    private String issuer;
    /**
     * 过期时间 (单位：秒)
     */
    private long expiration;

    @Override
    public String getSecretKey() {
        return secretKey;
    }

    @Override
    public String getIssuer() {
        return issuer;
    }

    @Override
    public Long getExpiration() {
        return expiration;
    }
}
