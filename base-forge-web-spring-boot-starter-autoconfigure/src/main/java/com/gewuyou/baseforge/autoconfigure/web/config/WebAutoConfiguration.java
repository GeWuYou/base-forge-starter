package com.gewuyou.baseforge.autoconfigure.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.autoconfigure.redis.service.CacheService;
import com.gewuyou.baseforge.autoconfigure.web.aspect.IdempotentAspect;
import com.gewuyou.baseforge.autoconfigure.web.aspect.ReadWriteLockAspect;
import com.gewuyou.baseforge.autoconfigure.web.config.entity.PageProperties;
import com.gewuyou.baseforge.autoconfigure.web.config.entity.WebStarterProperties;
import com.gewuyou.baseforge.autoconfigure.web.handler.GlobalExceptionHandler;
import com.gewuyou.baseforge.autoconfigure.web.interceptor.AccessLimitInterceptor;
import com.gewuyou.baseforge.autoconfigure.web.interceptor.PaginationInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web 自动配置
 *
 * @author gewuyou
 * @since 2024-11-13 11:01:23
 */
@EnableAutoConfiguration
@Configuration
@EnableConfigurationProperties({PageProperties.class, WebStarterProperties.class})
@Slf4j
public class WebAutoConfiguration implements WebMvcConfigurer {

    private final AccessLimitInterceptor accessLimitInterceptor;
    private final PaginationInterceptor paginationInterceptor;

    public WebAutoConfiguration(
            @Lazy
            AccessLimitInterceptor accessLimitInterceptor,
            @Lazy
            PaginationInterceptor paginationInterceptor
            ) {
        this.accessLimitInterceptor = accessLimitInterceptor;
        this.paginationInterceptor = paginationInterceptor;
    }

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations and resource handler requests.
     * Interceptors can be registered to apply to all requests or be limited
     * to a subset of URL patterns.
     *
     * @param registry the InterceptorRegistry to add interceptors to
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        log.info("开始注册拦截器");
        log.info("添加访问限制拦截器");
        registry.addInterceptor(accessLimitInterceptor);
        log.info("添加分页拦截器");
        registry.addInterceptor(paginationInterceptor);
        log.info("注册拦截器完成");
    }

    /**
     * 创建 AccessLimitInterceptor
     * @param cacheService 缓存服务
     * @param objectMapper 对象映射器
     * @return AccessLimitInterceptor
     */
    @Bean
    public AccessLimitInterceptor createAccessLimitInterceptor(
            CacheService cacheService,
            ObjectMapper objectMapper) {
        return new AccessLimitInterceptor(cacheService,objectMapper);
    }

    /**
     * 创建 IdempotentAspect
     * @param cacheService 缓存服务
     * @return IdempotentAspect
     */
    @Bean
    public IdempotentAspect idempotentAspect(CacheService cacheService) {
        return new IdempotentAspect(cacheService);
    }

    /**
     * 创建 ReadWriteLockAspect
     * @param redissonClient Redisson 客户端
     * @return ReadWriteLockAspect
     */
    @Bean
    public ReadWriteLockAspect readWriteLockAspect(RedissonClient redissonClient) {
        return new ReadWriteLockAspect(redissonClient);
    }

    /**
     * 创建 PaginationInterceptor
     * @param pageProperties 分页配置
     * @return PaginationInterceptor
     */
    @Bean
    public PaginationInterceptor createPaginationInterceptor(PageProperties pageProperties) {
        return new PaginationInterceptor(pageProperties);
    }
    /**
     * 创建 GlobalExceptionHandler
     * @return GlobalExceptionHandler
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
