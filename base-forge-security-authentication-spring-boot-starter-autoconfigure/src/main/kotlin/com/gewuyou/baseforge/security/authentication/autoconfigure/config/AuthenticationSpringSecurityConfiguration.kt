package com.gewuyou.baseforge.security.authentication.autoconfigure.config


import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.SecurityAuthenticationProperties
import com.gewuyou.baseforge.security.authentication.entities.extension.cleanUnNeedConfig
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configurers.*
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler

/**
 * Spring Security 配置
 *
 * @author gewuyou
 * @since 2025-01-03 10:19:48
 */
@Configuration
class AuthenticationSpringSecurityConfiguration(
    private val filters: List<AbstractAuthenticationProcessingFilter>,
    private val authenticationExceptionHandler: AuthenticationEntryPoint,
    private val securityAuthenticationProperties: SecurityAuthenticationProperties,
    private val logoutSuccessHandler: LogoutSuccessHandler
) {
    /**
     * 登录过滤器链
     *
     * @param http               HttpSecurity
     * @param securityProperties 安全配置
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean(name = ["loginFilterChain"])
    @Throws(Exception::class)
    fun loginFilterChain(
        http: HttpSecurity,
        securityProperties: SecurityAuthenticationProperties
    ): SecurityFilterChain {
        // 使用securityMatcher 匹配用户标识凭证登录请求
        http
            .cleanUnNeedConfig()
            .logout { logout: LogoutConfigurer<HttpSecurity?> ->
                logout.logoutSuccessUrl(securityAuthenticationProperties.logoutUrl)
                    .logoutSuccessHandler(logoutSuccessHandler)
            }
            .exceptionHandling { exceptionHandling: ExceptionHandlingConfigurer<HttpSecurity?> ->  // 处理认证异常 这里不处理授权异常是因为这个模块专注于认证，授权异常由授权模块处理
                exceptionHandling
                    .authenticationEntryPoint(authenticationExceptionHandler)
            }
            .securityMatcher(securityProperties.baseUrl)
            .authorizeHttpRequests {
                it
                    .anyRequest()
                    .authenticated()
            }
        // 添加登录过滤器
        for (filter in filters) {
            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter::class.java)
        }
        return http.build()
    }
}
