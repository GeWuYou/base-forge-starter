package com.gewuyou.baseforge.security.authorization.autoconfigure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.exception.InternalException
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.JwtProperties
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.SecurityAuthorizationProperties
import com.gewuyou.baseforge.security.authorization.autoconfigure.filter.ReactiveAuthorizationFilter
import com.gewuyou.baseforge.security.authorization.autoconfigure.filter.ReactiveJwtAuthorizationFilter
import com.gewuyou.baseforge.security.authorization.autoconfigure.handler.ReactiveAuthorizationExceptionHandler
import com.gewuyou.baseforge.security.authorization.autoconfigure.handler.ReactiveAuthorizationHandler
import com.gewuyou.baseforge.security.authorization.autoconfigure.handler.ReactiveGlobalExceptionHandler
import com.gewuyou.baseforge.security.authorization.autoconfigure.manager.ReactiveDynamicAuthorizationManager
import com.gewuyou.baseforge.security.authorization.autoconfigure.service.JwtAuthorizationService
import com.gewuyou.baseforge.security.authorization.autoconfigure.service.impl.JwtAuthorizationServiceImpl
import com.gewuyou.baseforge.security.authorization.autoconfigure.validator.DefaultJwtValidator
import com.gewuyou.baseforge.security.authorization.autoconfigure.validator.JwtValidator
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.core.userdetails.ReactiveUserDetailsService
import org.springframework.security.web.server.authorization.AuthorizationContext
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono

/**
 *反应式安全授权自动配置
 *
 * @since 2025-02-03 15:23:53
 * @author gewuyou
 */
@Configuration
@AutoConfigureBefore(ReactiveAuthorizationSpringSecurityConfiguration::class)
@EnableConfigurationProperties(SecurityAuthorizationProperties::class, JwtProperties::class)
@ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "reactive")
class ReactiveSecurityAuthorizationAutoConfiguration {
    /**
    * 反应式全局异常处理器
    */
    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    fun createReactiveGlobalExceptionHandler(objectMapper: ObjectMapper, i18nMessageSource: MessageSource): WebExceptionHandler {
        log.info("创建反应式全局异常处理器...")
        return ReactiveGlobalExceptionHandler(objectMapper, i18nMessageSource)
    }
    /**
    * 反应式用户信息服务
    */
    @Bean
    @ConditionalOnMissingBean(ReactiveUserDetailsService::class)
    fun createReactiveUserDetailsService():ReactiveUserDetailsService {
        log.info("创建空的反应式用户信息服务...")
        // 返回一个空的用户服务，避免默认用户被创建
        return ReactiveUserDetailsService {
            // 对于任意查询的用户名，返回一个空的 Mono，表示不存在该用户
            Mono.empty()
        }
    }
    /**
     * 反应式授权异常处理器
     */
    @Bean
    @ConditionalOnMissingBean(ServerAccessDeniedHandler::class)
    fun createAccessDeniedHandler(objectMapper: ObjectMapper, i18nMessageSource: MessageSource): ServerAccessDeniedHandler {
        log.info("创建默认反应式授权异常处理器...")
        return ReactiveAuthorizationExceptionHandler(objectMapper, i18nMessageSource)
    }

    /**
    * 反应式动态授权处理器
    */
    @Bean
    @ConditionalOnMissingBean(ReactiveAuthorizationHandler::class)
    fun createReactiveAuthorizationHandler():ReactiveAuthorizationHandler {
        throw InternalException("请实现ReactiveAuthorizationHandler接口")
    }

    /**
    * 反应式动态授权管理器
    */
    @Bean
    @ConditionalOnMissingBean(ReactiveAuthorizationManager::class)
    fun createReactiveAuthorizationManager(
        reactiveAuthorizationHandler: ReactiveAuthorizationHandler
    ):ReactiveAuthorizationManager<AuthorizationContext> {
        log.info("创建默认反应式动态授权管理器...")
        return ReactiveDynamicAuthorizationManager(reactiveAuthorizationHandler)
    }

    /**
     * JWT授权服务
     */
    @Bean
    @ConditionalOnMissingBean(JwtAuthorizationService::class)
    fun createJwtAuthorizationService(
        jwtValidator: JwtValidator,
        cacheService: CacheService,
        jwtProperties: JwtProperties
    ): JwtAuthorizationService {
        log.info("创建JWT授权服务...")
        return JwtAuthorizationServiceImpl(jwtValidator, cacheService, jwtProperties)
    }

    /**
    * 反应式授权过滤器
    */
    @Bean
    @ConditionalOnMissingBean(ReactiveAuthorizationFilter::class)
    fun createReactiveJwtAuthorizationFilter(jwtAuthorizationService: JwtAuthorizationService,securityAuthorizationProperties: SecurityAuthorizationProperties):ReactiveAuthorizationFilter {
        log.info("创建反应式授权过滤器...")
        return ReactiveJwtAuthorizationFilter(jwtAuthorizationService,securityAuthorizationProperties)
    }

    /**
     * jwt验证器
     */
    @Bean
    @ConditionalOnMissingBean(JwtValidator::class)
    fun createJwtValidator(jwtProperties: JwtProperties, cacheService: CacheService): JwtValidator {
        log.info("创建JWT验证器...")
        return DefaultJwtValidator(jwtProperties, cacheService)
    }
}