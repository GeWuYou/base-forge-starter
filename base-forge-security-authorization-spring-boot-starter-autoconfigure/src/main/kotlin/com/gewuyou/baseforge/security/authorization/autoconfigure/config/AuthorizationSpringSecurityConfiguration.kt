package com.gewuyou.baseforge.security.authorization.autoconfigure.config

import com.gewuyou.baseforge.security.authentication.entities.extension.cleanUnNeedConfig
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.SecurityAuthorizationProperties
import com.gewuyou.baseforge.security.authorization.autoconfigure.filter.AuthorizationFilter
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter

/**
 *授权 Spring Security 配置
 *
 * @since 2025-01-08 10:18:17
 * @author gewuyou
 */
@Configuration
@AutoConfigureAfter(SecurityAuthorizationAutoConfiguration::class)
class AuthorizationSpringSecurityConfiguration(
    private val dynamicAuthorizationManager: AuthorizationManager<RequestAuthorizationContext>,
    private val jwtAuthorizationFilter: AuthorizationFilter,
    private val authorizationExceptionHandler: AccessDeniedHandler,
    private val authorizationProperties: SecurityAuthorizationProperties
) {
    /**
     * 请求过滤器
     */
    @Bean(name = ["requestFilterChain"])
    @Throws(Exception::class)
    fun requestFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            // 关闭不必要配置
            .cleanUnNeedConfig()
            // 额外关闭登出
            .logout { it.disable() }
            // 开启授权
            .authorizeHttpRequests {
                it
                    .requestMatchers(authorizationProperties.requestUrl)
                    .permitAll()
                    .anyRequest()
                    // 所有请求都需要经过授权
                    .access(dynamicAuthorizationManager)
            }
            // 异常处理
            .exceptionHandling {
                it.accessDeniedHandler(authorizationExceptionHandler)
            }
            .securityMatcher(authorizationProperties.requestUrl)
            .addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }
}