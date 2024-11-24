package com.gewuyou.util.config.entity;

/**
 * jwt配置接口
 *
 * @author gewuyou
 * @since 2024-11-20 17:58:26
 */
public interface IJwtProperties {
    String getSecretKey();
    String getIssuer();
    Long getExpiration();
}
