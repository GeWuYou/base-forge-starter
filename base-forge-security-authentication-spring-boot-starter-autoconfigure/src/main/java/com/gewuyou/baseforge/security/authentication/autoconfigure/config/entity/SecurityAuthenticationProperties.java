package com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全性身份验证属性
 *
 * @author gewuyou
 * @since 2025-01-03 10:12:23
 */
@ConfigurationProperties("base-forge.security.authentication")
@Setter
@Getter
public class SecurityAuthenticationProperties {
    /**
     * 登录基础地址
     */
    private String baseLoginUrl = "/auth/login";
    /**
     * 普通登录页面地址(用户唯一标识符：密码登录)
     */
    private String normalLoginUrl = "/normal";
    /**
     * 登出页面地址
     */
    private String logoutUrl = "/logout";
}
