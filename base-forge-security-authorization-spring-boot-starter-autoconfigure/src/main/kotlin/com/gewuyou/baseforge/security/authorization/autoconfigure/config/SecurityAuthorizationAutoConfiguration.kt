package com.gewuyou.baseforge.security.authorization.autoconfigure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.exception.InternalException
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.SecurityAuthorizationProperties
import com.gewuyou.baseforge.security.authorization.autoconfigure.filter.AuthorizationFilter
import com.gewuyou.baseforge.security.authorization.autoconfigure.filter.JwtAuthorizationFilter
import com.gewuyou.baseforge.security.authorization.autoconfigure.handler.AuthorizationExceptionHandler
import com.gewuyou.baseforge.security.authorization.autoconfigure.handler.AuthorizationHandler
import com.gewuyou.baseforge.security.authorization.autoconfigure.manager.DynamicAuthorizationManager
import com.gewuyou.baseforge.security.authorization.autoconfigure.service.JwtAuthorizationService
import org.springframework.boot.autoconfigure.AutoConfigureBefore
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.access.intercept.RequestAuthorizationContext

/**
 *安全授权自动配置
 *
 * @since 2025-01-08 11:03:43
 * @author gewuyou
 */
@Configuration
@AutoConfigureBefore(AuthorizationSpringSecurityConfiguration::class)
@EnableConfigurationProperties(SecurityAuthorizationProperties::class)
class SecurityAuthorizationAutoConfiguration {
    /**
    * 授权异常处理器
    */
    @Bean
    @ConditionalOnMissingBean(AccessDeniedHandler::class)
    fun createAccessDeniedHandler(objectMapper: ObjectMapper,i18nMessageSource: MessageSource):AccessDeniedHandler {
        log.info("创建授权异常处理器...")
        return AuthorizationExceptionHandler(objectMapper,i18nMessageSource)
    }
    /**
    * 动态授权处理器
    */
    @Bean
    @ConditionalOnMissingBean(AuthorizationHandler::class)
    fun createAuthorizationHandler():AuthorizationHandler {
        throw InternalException("请实现AuthorizationHandler接口")
    }
    /**
    * 动态授权管理器
    */
    @Bean
    @ConditionalOnMissingBean(AuthorizationManager::class)
    fun createAuthorizationManager(authorizationHandler: AuthorizationHandler):AuthorizationManager<RequestAuthorizationContext> {
        log.info("创建动态授权管理器...")
        return DynamicAuthorizationManager(authorizationHandler)
    }

    /**
    * JWT授权服务
    */
    @Bean
    @ConditionalOnMissingBean(JwtAuthorizationService::class)
    fun createJwtAuthorizationService():JwtAuthorizationService {
        throw InternalException("请实现JwtAuthorizationService接口")
    }
    /**
    * 授权过滤器
    */
    @Bean
    @ConditionalOnMissingBean(AuthorizationFilter::class)
    fun createAuthorizationFilter(jwtAuthorizationService: JwtAuthorizationService):AuthorizationFilter {
        log.info("创建授权过滤器...")
        return JwtAuthorizationFilter(jwtAuthorizationService)
    }
}