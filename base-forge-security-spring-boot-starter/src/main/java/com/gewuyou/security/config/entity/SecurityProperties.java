package com.gewuyou.security.config.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 安全属性
 *
 * @author gewuyou
 * @since 2024-11-24 20:18:47
 */
@ConfigurationProperties(prefix = "base-forge.security")
@Data
public class SecurityProperties {
    /**
     * 过滤链匹配规则前缀
     */
    private String loginUrlPrefix;
    /**
     * 普通登录地址
     */
    private String normalLoginUrl;
}
