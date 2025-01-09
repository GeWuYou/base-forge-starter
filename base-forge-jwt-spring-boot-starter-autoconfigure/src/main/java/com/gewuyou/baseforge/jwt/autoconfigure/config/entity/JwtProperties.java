package com.gewuyou.baseforge.jwt.autoconfigure.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * jwt配置
 *
 * @author gewuyou
 * @since 2024-12-31 15:26:52
 */
@ConfigurationProperties(prefix = "base-forge.security.jwt")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtProperties {
    /**
     * 密钥
     */
    private String secretKey = "base-forge-security-secret-key";
    /**
     * 颁发者
     */
    private String issuer = "base-forge-security-issuer";
    /**
     * 访问令牌过期时间 (单位：毫秒, 默认15分钟)
     */
    private long expiration = 900_000;
    /**
     * 刷新token过期时间 (单位：毫秒, 默认30天)
     */
    private long refreshTokenExpiration = 2_592_000_000L;
}
