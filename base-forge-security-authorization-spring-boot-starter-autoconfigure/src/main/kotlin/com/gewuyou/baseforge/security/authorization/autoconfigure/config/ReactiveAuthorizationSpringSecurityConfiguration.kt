package com.gewuyou.baseforge.security.authorization.autoconfigure.config

import com.gewuyou.baseforge.security.authentication.entities.extension.cleanUnNeedConfig
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.SecurityAuthorizationProperties
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authorization.AuthorizationContext
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers

/**
 *反应式授权 Spring Security 配置
 *
 * @since 2025-02-03 11:04:59
 * @author gewuyou
 */
@Configuration
@EnableWebFluxSecurity
@AutoConfigureAfter(ReactiveSecurityAuthorizationAutoConfiguration::class)
@ConditionalOnProperty(prefix = "base-forge.security.authorization", name = ["isWebFlux"], havingValue = "true")
class ReactiveAuthorizationSpringSecurityConfiguration(
    private val authorizationProperties: SecurityAuthorizationProperties,
    private val reactiveAuthorizationManager: ReactiveAuthorizationManager<AuthorizationContext>,
    private val reactiveAccessDeniedHandler: ServerAccessDeniedHandler
) {
    /**
     * 请求过滤器
     */
    @Bean(name = ["requestFilterChain"])
    @Throws(Exception::class)
    fun createSecurityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .cleanUnNeedConfig()
            .authorizeExchange {
                it
                    .matchers(
                        ServerWebExchangeMatchers
                            .pathMatchers(*authorizationProperties.ignored)
                    ).permitAll()
                    .anyExchange()
                    .access(reactiveAuthorizationManager)
            }
            .exceptionHandling {
                it.accessDeniedHandler(reactiveAccessDeniedHandler)
            }
            .securityMatcher(
                ServerWebExchangeMatchers
                    .pathMatchers(authorizationProperties.requestUrl)
            )
            .build()
    }
}