package com.gewuyou.baseforge.autoconfigure.i18n.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * 本地化配置
 *
 * @author gewuyou
 * @since 2024-11-11 00:46:01
 */
@Configuration
@Slf4j
public class I18nAutoConfiguration {
    public static final String MESSAGE_SOURCE_BEAN_NAME = "i18nMessageSource";

    @Bean(name = MESSAGE_SOURCE_BEAN_NAME)
    @ConditionalOnMissingBean(name = MESSAGE_SOURCE_BEAN_NAME)
    public MessageSource messageSource() {
        log.info("开始加载 I18n 配置...");
        ReloadableResourceBundleMessageSource messageSource = new ReloadableResourceBundleMessageSource();
        // 动态扫描所有 i18n 子目录下的 messages.properties 文件
        String basePath = "classpath*:i18n/**/messages";
        List<String> baseNames = scanBaseNames(basePath);

        // 设置文件路径到 messageSource
        messageSource.setBasenames(baseNames.toArray(new String[0]));
        messageSource.setDefaultEncoding("UTF-8");
        log.info("I18n 配置加载完成...");
        return messageSource;
    }

    private List<String> scanBaseNames(String basePath) {
        List<String> baseNames = new ArrayList<>();
        log.info("开始扫描 I18n 文件 {}" , basePath);
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(basePath + "*.properties");
            for (Resource resource : resources) {
                String path = resource.getURI().toString();
                log.info("找到 I18n 文件路径: {}" , path);
                // 转换路径为 Spring 的 basename 格式（去掉 .properties 后缀）
                String baseName = path.substring(0, path.lastIndexOf(".properties"));
                if (!baseNames.contains(baseName)) {
                    baseNames.add(baseName);
                }
            }
        } catch (IOException e) {
            log.error("无法扫描 I18n 文件", e);
        }
        return baseNames;
    }

    @Bean
    public LocaleResolver localeResolver() {
        AcceptHeaderLocaleResolver localeResolver = new AcceptHeaderLocaleResolver();
        localeResolver.setDefaultLocale(Locale.CHINA);
        return localeResolver;
    }
}
