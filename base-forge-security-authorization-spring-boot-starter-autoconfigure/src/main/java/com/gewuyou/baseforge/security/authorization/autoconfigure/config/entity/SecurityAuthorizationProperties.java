package com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全授权属性
 *
 * @author gewuyou
 * @since 2025-01-08 14:42:33
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("base-forge.security.authorization")
public class SecurityAuthorizationProperties {
    /**
     * 排除路径
     */
    private String[] ignored = new String[0];
    /**
     * 进行鉴权的请求路径
     */
    private String requestUrl = "/auth/z";
}