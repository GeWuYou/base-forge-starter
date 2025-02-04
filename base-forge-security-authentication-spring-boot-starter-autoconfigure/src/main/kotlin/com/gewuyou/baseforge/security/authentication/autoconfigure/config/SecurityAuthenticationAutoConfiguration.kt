package com.gewuyou.baseforge.security.authentication.autoconfigure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.exception.InternalException
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.jwt.generation.autoconfigure.provider.JwtTokenProvider
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.JwtProperties
import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.SecurityAuthenticationProperties
import com.gewuyou.baseforge.security.authentication.autoconfigure.filter.NormalAuthenticationFilter
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.AuthenticationExceptionHandler
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LoginFailJsonResponseHandler
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LoginSuccessJsonResponseHandler
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LogoutSuccessJsonResponseHandler
import com.gewuyou.baseforge.security.authentication.autoconfigure.provider.DefaultJwtTokenProvider
import com.gewuyou.baseforge.security.authentication.autoconfigure.provider.NormalAuthenticationProvider
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.AuthenticationUserDetailsService
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.JwtAuthenticationService
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.impl.JwtAuthenticationServiceImpl
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper
import org.springframework.security.core.userdetails.UserCache
import org.springframework.security.core.userdetails.cache.NullUserCache
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher

/**
 * 安全认证 自动配置
 *
 * @author gewuyou
 * @since 2025-01-03 10:17:19
 */
@Configuration
@EnableConfigurationProperties(SecurityAuthenticationProperties::class,JwtProperties::class)
class SecurityAuthenticationAutoConfiguration {
    /**
     * jwt服务类
     *
     */
    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationService::class)
    fun createJwtService(jwtTokenProvider: JwtTokenProvider,cacheService: CacheService,jwtProperties: JwtProperties): JwtAuthenticationService {
        log.info("创建jwt认证服务默认实现...")
        return JwtAuthenticationServiceImpl(jwtTokenProvider,cacheService,jwtProperties)
    }

    /**
    * Jwt令牌提供程序
    */
    @Bean
    @ConditionalOnMissingBean(JwtTokenProvider::class)
    fun createJwtTokenProvider(jwtProperties: JwtProperties,cacheService: CacheService):JwtTokenProvider {
        return DefaultJwtTokenProvider(jwtProperties,cacheService)
    }

    /**
     * 用户信息服务
     *
     * @throws InternalException 内部异常
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationUserDetailsService::class)
    fun createUserDetailsService(): AuthenticationUserDetailsService {
        throw InternalException("请实现UserDetailsService接口用于提供用户信息服务!")
    }

    /**
     * 登录成功处理器
     *
     * @param objectMapper objectMapper
     * @return 登录成功处理器
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler::class)
    fun createLoginSuccessHandler(
        objectMapper: ObjectMapper,
        jwtAuthenticationService: JwtAuthenticationService,
        authenticationUserDetailsService: AuthenticationUserDetailsService,
        @Qualifier("i18nMessageSource") i18nMessageSource: MessageSource
    ): AuthenticationSuccessHandler {
        return LoginSuccessJsonResponseHandler(
            objectMapper,
            jwtAuthenticationService,
            authenticationUserDetailsService,
            i18nMessageSource
        )
    }

    /**
     * 登录失败处理器
     *
     * @param objectMapper objectMapper
     * @return 登录失败处理器
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler::class)
    fun createLoginFailHandler(
        objectMapper: ObjectMapper,
        @Qualifier("i18nMessageSource") i18nMessageSource: MessageSource
    ): AuthenticationFailureHandler {
        return LoginFailJsonResponseHandler(objectMapper, i18nMessageSource)
    }

    /**
     * 创建普通登录认证过滤器 用于处理普通登录请求
     *
     * @param properties                   安全认证配置
     * @param authenticationProvider       认证提供器
     * @param authenticationSuccessHandler 认证成功处理器
     * @param authenticationFailureHandler 认证失败处理器
     * @param objectMapper                 json对象映射器
     * @return 普通登录认证过滤器
     */
    @Bean("normalAuthenticationFilter")
    fun createNormalAuthenticationFilter(
        properties: SecurityAuthenticationProperties,
        authenticationProvider: NormalAuthenticationProvider,
        authenticationSuccessHandler: AuthenticationSuccessHandler?,
        authenticationFailureHandler: AuthenticationFailureHandler?,
        objectMapper: ObjectMapper?
    ): NormalAuthenticationFilter {
        return NormalAuthenticationFilter(
            AntPathRequestMatcher(
                properties.normalLoginUrl,
                HttpMethod.POST.name()
            ),
            ProviderManager(
                listOf<AuthenticationProvider>(authenticationProvider)
            ),
            authenticationSuccessHandler,
            authenticationFailureHandler,
            objectMapper
        )
    }

    /**
     * 用户信息缓存 默认为空实现
     *
     * @return 用户信息缓存
     */
    @Bean
    @ConditionalOnMissingBean(UserCache::class)
    fun createUserCache(): UserCache {
        return NullUserCache()
    }

    /**
     * 用户权限映射 默认为空实现
     *
     * @return 用户权限映射器
     */
    @Bean
    @ConditionalOnMissingBean(GrantedAuthoritiesMapper::class)
    fun createGrantedAuthoritiesMapper(): GrantedAuthoritiesMapper {
        return NullAuthoritiesMapper()
    }

    /**
     * 默认的密码加密器为BCryptPasswordEncoder
     *
     * @return 密码加密器
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder::class)
    fun createPasswordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    /**
     * 普通登录认证提供器
     *
     * @param userCache                        用户信息缓存
     * @param authoritiesMapper                权限映射器
     * @param passwordEncoder                  密码加密器
     * @param authenticationUserDetailsService 用户信息服务
     * @return 普通登录认证提供器
     */
    @Bean
    fun createNormalAuthenticationProvider(
        userCache: UserCache?,
        authoritiesMapper: GrantedAuthoritiesMapper?,
        passwordEncoder: PasswordEncoder?,
        authenticationUserDetailsService: AuthenticationUserDetailsService?
    ): NormalAuthenticationProvider {
        return NormalAuthenticationProvider(
            userCache,
            authoritiesMapper,
            passwordEncoder,
            authenticationUserDetailsService
        )
    }

    /**
     * 认证异常处理器
     *
     * @param objectMapper objectMapper
     * @return 认证异常处理器
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint::class)
    fun createAuthenticationEntryPoint(
        objectMapper: ObjectMapper,
        @Qualifier("i18nMessageSource") i18nMessageSource: MessageSource
    ): AuthenticationEntryPoint {
        return AuthenticationExceptionHandler(objectMapper, i18nMessageSource)
    }

    /**
     * 登出成功处理器
     *
     * @param objectMapper             json对象映射器
     * @param jwtAuthenticationService jwt服务
     * @return 登出成功处理器
     */
    @Bean
    @ConditionalOnMissingBean(LogoutSuccessHandler::class)
    fun createLogoutSuccessHandler(
        objectMapper: ObjectMapper,
        jwtAuthenticationService: JwtAuthenticationService,
        @Qualifier("i18nMessageSource") i18nMessageSource: MessageSource
    ): LogoutSuccessHandler {
        return LogoutSuccessJsonResponseHandler(objectMapper, jwtAuthenticationService, i18nMessageSource)
    }
}
