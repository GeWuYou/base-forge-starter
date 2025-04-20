package com.gewuyou.baseforge.autoconfigure.i18n.config;

import com.gewuyou.baseforge.autoconfigure.i18n.config.entity.I18nProperties;
import com.gewuyou.baseforge.autoconfigure.i18n.filter.ReactiveLocaleResolver;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;

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
@EnableConfigurationProperties({I18nProperties.class})
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
        log.info("开始扫描 I18n 文件 {}", basePath);
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(basePath + "*.properties");
            for (Resource resource : resources) {
                String path = resource.getURI().toString();
                log.info("找到 I18n 文件路径: {}", path);
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
    @ConditionalOnClass(name = {"org.springframework.web.servlet.DispatcherServlet"})
    public LocaleResolver localeResolver(I18nProperties i18nProperties) {
        return new AcceptHeaderLocaleResolver() {
            @NotNull
            @Override
            public Locale resolveLocale(@NotNull HttpServletRequest request) {
                log.info("开始解析URL参数 lang 语言配置...");
                // 先检查URL参数 ?lang=xx
                String lang = request.getParameter(i18nProperties.getLangRequestParameter());
                if (StringUtils.hasText(lang)) {
                    return Locale.forLanguageTag(lang);
                }
                // 设置默认语言为简体中文
                this.setDefaultLocale(Locale.forLanguageTag(i18nProperties.getDefaultLocale()));
                // 返回请求头 Accept-Language 的语言配置
                return super.resolveLocale(request);
            }
        };
    }

    @Bean
    @ConditionalOnProperty(name = {"spring.main.web-application-type"}, havingValue = "servlet", matchIfMissing = true)
    public LocaleChangeInterceptor localeChangeInterceptor(I18nProperties i18nProperties) {
        log.info("创建区域设置更改拦截器...");
        LocaleChangeInterceptor interceptor = new LocaleChangeInterceptor();
        // 设置 URL 参数名，例如 ?lang=en 或 ?lang=zh
        interceptor.setParamName(i18nProperties.getLangRequestParameter());
        return interceptor;
    }

    @Bean
    @ConditionalOnProperty(name = {"spring.main.web-application-type"}, havingValue = "reactive")
    public ReactiveLocaleResolver createReactiveLocaleResolver(I18nProperties i18nProperties) {
        log.info("创建 WebFlux 区域设置解析器...");
        return new ReactiveLocaleResolver(i18nProperties);
    }
}
