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
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.web.server.authorization.AuthorizationContext
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler

/**
 *反应式安全授权自动配置
 *
 * @since 2025-02-03 15:23:53
 * @author gewuyou
 */
@Configuration
@AutoConfigureBefore(ReactiveAuthorizationSpringSecurityConfiguration::class)
@EnableConfigurationProperties(SecurityAuthorizationProperties::class, JwtProperties::class)
@ConditionalOnProperty(prefix = "base-forge.security.authorization", name = ["isWebFlux"], havingValue = "true")
class ReactiveSecurityAuthorizationAutoConfiguration {
    /**
     * 反应式授权异常处理器
     */
    @Bean
    @ConditionalOnMissingBean(ServerAccessDeniedHandler::class)
    fun createAccessDeniedHandler(objectMapper: ObjectMapper, i18nMessageSource: MessageSource): ServerAccessDeniedHandler {
        log.info("创建授权异常处理器...")
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
        log.info("创建动态授权管理器...")
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
    fun createReactiveJwtAuthorizationFilter(jwtAuthorizationService: JwtAuthorizationService):ReactiveAuthorizationFilter {
        log.info("创建授权过滤器...")
        return ReactiveJwtAuthorizationFilter(jwtAuthorizationService)
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