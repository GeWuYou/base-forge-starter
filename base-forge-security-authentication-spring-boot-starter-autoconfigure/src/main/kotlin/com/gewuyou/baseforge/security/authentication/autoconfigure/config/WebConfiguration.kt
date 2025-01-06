package com.gewuyou.baseforge.security.authentication.autoconfigure.config

import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.interceptor.DefaultIdempotentLoginRequestInterceptor
import com.gewuyou.baseforge.security.authentication.autoconfigure.interceptor.IdempotentLoginRequestInterceptor
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

/**
 *Web 配置
 *
 * @since 2025-01-06 11:13:45
 * @author gewuyou
 */
@Configuration
@EnableAutoConfiguration
class WebConfiguration:WebMvcConfigurer {
    /**
    * 登录请求幂等拦截器
    */
    @Bean
    @ConditionalOnMissingBean(IdempotentLoginRequestInterceptor::class)
    fun createIdempotentLoginRequestInterceptor(cacheService: CacheService):IdempotentLoginRequestInterceptor {
        return DefaultIdempotentLoginRequestInterceptor(cacheService)
    }
}