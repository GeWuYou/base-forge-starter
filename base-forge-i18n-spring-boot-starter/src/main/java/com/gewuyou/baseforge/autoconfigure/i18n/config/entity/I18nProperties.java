package com.gewuyou.baseforge.autoconfigure.i18n.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * i18n属性
 *
 * @author gewuyou
 * @since 2025-02-18 23:59:57
 */
@ConfigurationProperties(prefix = "base-forge.i18n")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class I18nProperties {
    /**
     * 默认语言
     */
    private String defaultLocale = "zh_CN";
    /**
     * 语言请求参数名
     */
    private String langRequestParameter = "lang";
}
