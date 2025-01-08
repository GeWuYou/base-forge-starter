package com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全授权属性
 *
 * @author gewuyou
 * @since 2025-01-08 14:42:33
 */
@ConfigurationProperties("base-forge.security.authorization")
@Setter
@Getter
public class SecurityAuthorizationProperties {
    /**
     * 排除的URL
     */
    private String[] ignoredUrls = new String[0];

    /**
     * 请求URL(默认鉴权请求URL：/api)
     */
   private String requestUrl = "/api";
}
