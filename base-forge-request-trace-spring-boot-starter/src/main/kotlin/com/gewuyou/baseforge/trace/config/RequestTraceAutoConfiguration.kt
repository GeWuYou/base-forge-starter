package com.gewuyou.baseforge.trace.config

import com.gewuyou.baseforge.trace.decorator.RequestIdTaskDecorator
import com.gewuyou.baseforge.trace.filter.ReactiveRequestIdFilter
import com.gewuyou.baseforge.trace.filter.RequestIdFilter
import com.gewuyou.baseforge.trace.interceptor.FeignRequestIdInterceptor
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.cloud.openfeign.FeignAutoConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *请求跟踪自动配置
 *
 * @since 2025-03-17 16:33:40
 * @author gewuyou
 */
@Configuration
class RequestTraceAutoConfiguration {
    /**
     * ✅ Spring MVC 过滤器（仅当 Spring MVC 存在时生效）
     */
    @Bean
    @ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "servlet", matchIfMissing = true)
    @ConditionalOnMissingBean
    fun requestIdFilter(): RequestIdFilter = RequestIdFilter()

    /**
     * ✅ Spring WebFlux 过滤器（仅当 Spring WebFlux 存在时生效）
     */
    @Bean
    @ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "reactive")
    @ConditionalOnMissingBean
    fun reactiveRequestIdFilter(): ReactiveRequestIdFilter = ReactiveRequestIdFilter()

    /**
     * Feign 拦截器（仅当 Feign 存在时生效）
     */
    @Bean
    @ConditionalOnClass(FeignAutoConfiguration::class)
    @ConditionalOnMissingBean
    fun feignRequestIdInterceptor(): FeignRequestIdInterceptor = FeignRequestIdInterceptor()

    /**
     * 线程池装饰器（用于 @Async）
     */
    @Bean
    @ConditionalOnMissingBean
    fun requestIdTaskDecorator(): RequestIdTaskDecorator = RequestIdTaskDecorator()
}