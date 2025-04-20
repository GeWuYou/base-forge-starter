package com.gewuyou.baseforge.security.authorization.autoconfigure.config

import com.gewuyou.baseforge.autoconfigure.i18n.filter.ReactiveLocaleResolver
import com.gewuyou.baseforge.security.authentication.entities.extension.cleanUnNeedConfig
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.SecurityAuthorizationProperties
import com.gewuyou.baseforge.security.authorization.autoconfigure.filter.ReactiveAuthorizationFilter
import com.gewuyou.baseforge.trace.filter.ReactiveRequestIdFilter
import org.springframework.boot.autoconfigure.AutoConfigureAfter
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
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
@ConditionalOnProperty(name = ["spring.main.web-application-type"], havingValue = "reactive")
class ReactiveAuthorizationSpringSecurityConfiguration(
    private val authorizationProperties: SecurityAuthorizationProperties,
    private val reactiveAuthorizationManager: ReactiveAuthorizationManager<AuthorizationContext>,
    private val reactiveAccessDeniedHandler: ServerAccessDeniedHandler,
    private val reactiveJwtAuthorizationFilter: ReactiveAuthorizationFilter,
    private val reactiveRequestIdFilter: ReactiveRequestIdFilter,
    private val reactiveLocaleResolver: ReactiveLocaleResolver
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
                if (authorizationProperties.ignored.isNotEmpty()) {
                    it
                        .matchers(
                            ServerWebExchangeMatchers
                                .pathMatchers(*authorizationProperties.ignored)
                        ).permitAll()
                }
                // ✅ 允许 OPTIONS 预检请求
                it.matchers(ServerWebExchangeMatchers.pathMatchers(HttpMethod.OPTIONS, "/**")).permitAll()
                it
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
            // ✅ 在 `SecurityWebFiltersOrder.FIRST` 之前添加请求 ID 过滤器
            .addFilterAt(reactiveRequestIdFilter, SecurityWebFiltersOrder.FIRST)
            // ✅ 在 `SecurityWebFiltersOrder.FIRST` 之前添加反应式语言解析器
            .addFilterAfter(reactiveLocaleResolver, SecurityWebFiltersOrder.FIRST)
            // ✅ 在 `SecurityWebFiltersOrder.AUTHENTICATION` 之前添加 JWT 过滤器
            .addFilterAt(reactiveJwtAuthorizationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            .build()
    }
}