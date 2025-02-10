package com.gewuyou.baseforge.security.authentication.autoconfigure.config

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.interceptor.DefaultIdempotentLoginRequestInterceptor
import com.gewuyou.baseforge.security.authentication.autoconfigure.interceptor.IdempotentLoginRequestInterceptor
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
class WebConfiguration:WebMvcConfigurer {
    /**
    * 登录请求幂等拦截器
    */
    @Bean
    @ConditionalOnMissingBean(IdempotentLoginRequestInterceptor::class)
    fun createIdempotentLoginRequestInterceptor(cacheService: CacheService):IdempotentLoginRequestInterceptor {
        log.info("创建默认的登录请求幂等拦截器...")
        return DefaultIdempotentLoginRequestInterceptor(cacheService)
    }
}