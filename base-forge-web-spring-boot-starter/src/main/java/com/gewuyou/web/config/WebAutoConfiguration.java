package com.gewuyou.web.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.redis.service.ICacheService;
import com.gewuyou.web.aspect.IdempotentAspect;
import com.gewuyou.web.aspect.ReadWriteLockAspect;
import com.gewuyou.web.handler.GlobalExceptionHandler;
import com.gewuyou.web.interceptor.AccessLimitInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.MessageSource;
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
@Slf4j
public class WebAutoConfiguration implements WebMvcConfigurer {

    private final AccessLimitInterceptor accessLimitInterceptor;

    public WebAutoConfiguration(@Lazy AccessLimitInterceptor accessLimitInterceptor) {
        this.accessLimitInterceptor = accessLimitInterceptor;
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
        registry.addInterceptor(accessLimitInterceptor);
    }

    /**
     * 创建 AccessLimitInterceptor
     * @param cacheService 缓存服务
     * @param objectMapper 对象映射器
     * @param messageSource 消息源
     * @return AccessLimitInterceptor
     */
    @Bean
    public AccessLimitInterceptor createAccessLimitInterceptor(ICacheService cacheService, ObjectMapper objectMapper, MessageSource messageSource) {
        return new AccessLimitInterceptor(cacheService,objectMapper,messageSource);
    }

    /**
     * 创建 IdempotentAspect
     * @param cacheService 缓存服务
     * @return IdempotentAspect
     */
    @Bean
    public IdempotentAspect idempotentAspect(ICacheService cacheService) {
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
     * 创建 GlobalExceptionHandler
     * @return GlobalExceptionHandler
     */
    @Bean
    public GlobalExceptionHandler globalExceptionHandler() {
        return new GlobalExceptionHandler();
    }
}
