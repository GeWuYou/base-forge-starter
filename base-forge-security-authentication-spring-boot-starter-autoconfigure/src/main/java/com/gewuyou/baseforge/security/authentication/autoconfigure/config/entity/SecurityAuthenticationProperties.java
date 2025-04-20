package com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全性身份验证属性
 *
 * @author gewuyou
 * @since 2025-01-15 22:07:46
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties("base-forge.security.authentication")
public class SecurityAuthenticationProperties {
    /**
     * 基础地址
     */
    private String baseUrl = "/auth/c";
    /**
     * 登录地址
     */
    private String loginUrl = "/login";
    /**
     * 登出地址
     */
    private String logoutUrl = "/logout";
}
