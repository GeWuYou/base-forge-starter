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
     * 登录基础地址
     */
    private String baseLoginUrl = "/auth/c/login";
    /**
     * 普通登录页面地址
     */
    private String normalLoginUrl = "/normal";
    /**
     * 登出页面地址
     */
    private String logoutUrl = "/logout";
}
