package com.gewuyou.baseforge.autoconfigure.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.autoconfigure.i18n.config.I18nAutoConfiguration;
import com.gewuyou.baseforge.autoconfigure.web.aspect.IdempotentAspect;
import com.gewuyou.baseforge.autoconfigure.web.aspect.MethodRecordingAspect;
import com.gewuyou.baseforge.autoconfigure.web.aspect.ReadWriteLockAspect;
import com.gewuyou.baseforge.autoconfigure.web.config.entity.PageProperties;
import com.gewuyou.baseforge.autoconfigure.web.config.entity.ReadWriteLockProperties;
import com.gewuyou.baseforge.autoconfigure.web.handler.GlobalExceptionHandler;
import com.gewuyou.baseforge.autoconfigure.web.interceptor.AccessLimitInterceptor;
import com.gewuyou.baseforge.autoconfigure.web.interceptor.PaginationInterceptor;
import com.gewuyou.baseforge.autoconfigure.web.mapping.ApiVersionRequestMappingHandlerMapping;
import com.gewuyou.baseforge.redis.service.CacheService;
import com.gewuyou.baseforge.trace.filter.RequestIdFilter;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

/**
 * Web 自动 配置类
 *
 * @author gewuyou
 * @since 2025-01-19 19:29:39
 */
@Configuration
@EnableConfigurationProperties({PageProperties.class, ReadWriteLockProperties.class})
@Slf4j
public class WebAutoConfiguration {
    /**
     * 创建 ApiVersionRequestMappingHandlerMapping
     * @return 映射处理器
     */
    @Bean
    public ApiVersionRequestMappingHandlerMapping createApiVersionRequestMappingHandlerMapping() {
        log.info("创建 API 版本请求映射处理程序映射");
        return new ApiVersionRequestMappingHandlerMapping();
    }
    /**
     * 创建 AccessLimitInterceptor
     *
     * @param cacheService 缓存服务
     * @param objectMapper 对象映射器
     * @return AccessLimitInterceptor
     */
    @Bean
    public AccessLimitInterceptor createAccessLimitInterceptor(
            CacheService cacheService,
            ObjectMapper objectMapper,
            @Qualifier(I18nAutoConfiguration.MESSAGE_SOURCE_BEAN_NAME)
            MessageSource i18nMessageSource
    ) {
        log.info("创建 访问限制拦截器");
        return new AccessLimitInterceptor(cacheService, objectMapper, i18nMessageSource);
    }

    /**
     * 创建 IdempotentAspect
     *
     * @param cacheService 缓存服务
     * @return IdempotentAspect
     */
    @Bean
    public IdempotentAspect idempotentAspect(CacheService cacheService) {
        log.info("创建 幂等切面");
        return new IdempotentAspect(cacheService);
    }

    /**
     * 创建 MethodRecordingAspect
     * @return MethodRecordingAspect
     */
    @Bean
    public MethodRecordingAspect createMethodRecordingAspect() {
        log.info("创建 方法记录切面");
        return new MethodRecordingAspect();
    }

    /**
     * 创建 ReadWriteLockAspect
     *
     * @param redissonClient Redisson 客户端
     * @return ReadWriteLockAspect
     */
    @Bean
    @ConditionalOnProperty(name = "base-forge.web.read-write-lock.enabled", havingValue = "true")
    public ReadWriteLockAspect readWriteLockAspect(RedissonClient redissonClient) {
        log.info("创建 ReadWriteLockAspect");
        return new ReadWriteLockAspect(redissonClient);
    }

    /**
     * 创建 PaginationInterceptor
     *
     * @param pageProperties 分页配置
     * @return PaginationInterceptor
     */
    @Bean
    @ConditionalOnProperty(name = "base-forge.web.page.enabled", havingValue = "true")
    public PaginationInterceptor createPaginationInterceptor(PageProperties pageProperties) {
        log.info("创建 PaginationInterceptor");
        return new PaginationInterceptor(pageProperties);
    }

    /**
     * 创建 GlobalExceptionHandler
     *
     * @return GlobalExceptionHandler
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler(@Qualifier(I18nAutoConfiguration.MESSAGE_SOURCE_BEAN_NAME) MessageSource i18nMessageSource) {
        log.info("创建 GlobalExceptionHandler");
        return new GlobalExceptionHandler(i18nMessageSource);
    }

    /**
     * 创建 RequestIdFilter
     * @param requestIdFilter 请求 ID 过滤器
     * @return FilterRegistrationBean
     */
    @Bean
    @ConditionalOnProperty(name = {"spring.main.web-application-type"}, havingValue = "servlet", matchIfMissing = true)
    @ConditionalOnMissingBean
    public FilterRegistrationBean<RequestIdFilter> createFilterRegistrationBean(RequestIdFilter requestIdFilter) {
        log.info("创建 RequestIdFilter");
        FilterRegistrationBean<RequestIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(requestIdFilter);
        // 默认拦截所有请求
        registrationBean.addUrlPatterns("/*");
        // 设置最高优先级
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        return registrationBean;
    }
}
