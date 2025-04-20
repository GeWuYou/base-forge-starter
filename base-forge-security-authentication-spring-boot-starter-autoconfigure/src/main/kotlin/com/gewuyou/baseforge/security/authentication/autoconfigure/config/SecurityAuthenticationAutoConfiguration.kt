package com.gewuyou.baseforge.security.authentication.autoconfigure.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.exception.InternalException
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authentication.autoconfigure.provider.JwtTokenProvider
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.JwtProperties
import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.SecurityAuthenticationProperties
import com.gewuyou.baseforge.security.authentication.autoconfigure.context.LoginRequestConverterContext
import com.gewuyou.baseforge.security.authentication.autoconfigure.factory.LoginRequestParserFactory
import com.gewuyou.baseforge.security.authentication.autoconfigure.filter.DefaultAuthenticationFilter
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.AuthenticationExceptionHandler
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LoginFailJsonResponseHandler
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LoginSuccessJsonResponseHandler
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LogoutSuccessJsonResponseHandler
import com.gewuyou.baseforge.security.authentication.autoconfigure.parser.FormLoginRequestParser
import com.gewuyou.baseforge.security.authentication.autoconfigure.parser.JsonLoginRequestParser
import com.gewuyou.baseforge.security.authentication.autoconfigure.provider.AbstractPrincipalPasswordAuthenticationProvider
import com.gewuyou.baseforge.security.authentication.autoconfigure.provider.DefaultJwtTokenProvider
import com.gewuyou.baseforge.security.authentication.autoconfigure.provider.UsernamePasswordAuthenticationProvider
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.JwtAuthenticationService
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.UserCacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.UserDetailsService
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.impl.JwtAuthenticationServiceImpl
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.impl.NullUserCacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.strategy.UsernamePasswordLoginRequestConverterStrategy
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.ApplicationContext
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper

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
@EnableConfigurationProperties(SecurityAuthenticationProperties::class, JwtProperties::class)
class SecurityAuthenticationAutoConfiguration {
    /**
     * 表单登录请求解析器
     */
    @Bean
    @ConditionalOnMissingBean(FormLoginRequestParser::class)
    fun createFromLoginRequestParser(): FormLoginRequestParser {
        log.info("创建默认表单登录请求解析器...")
        return FormLoginRequestParser()
    }

    /**
     * json登录请求解析器
     */
    @Bean
    @ConditionalOnMissingBean(JsonLoginRequestParser::class)
    fun createJsonLoginRequestParser(objectMapper: ObjectMapper): JsonLoginRequestParser {
        log.info("创建默认json登录请求解析器...")
        return JsonLoginRequestParser(objectMapper)
    }

    /**
     * 用户名密码登录请求转换器
     */
    @Bean
    @ConditionalOnMissingBean(UsernamePasswordLoginRequestConverterStrategy::class)
    fun createUsernamePasswordLoginRequestConverterStrategy(): UsernamePasswordLoginRequestConverterStrategy {
        log.info("创建默认用户名密码登录请求转换器...")
        return UsernamePasswordLoginRequestConverterStrategy()
    }

    /**
     * 登录请求解析器工厂
     */
    @Bean
    @ConditionalOnMissingBean(LoginRequestParserFactory::class)
    fun createLoginRequestParserFactory(applicationContext: ApplicationContext): LoginRequestParserFactory {
        log.info("创建默认登录请求解析器工厂...")
        return LoginRequestParserFactory(applicationContext)
    }

    /**
     * 登录请求转换策略上下文
     */
    @Bean
    @ConditionalOnMissingBean(LoginRequestConverterContext::class)
    fun createLoginRequestConverterContext(applicationContext: ApplicationContext): LoginRequestConverterContext {
        log.info("创建默认登录请求转换策略上下文...")
        return LoginRequestConverterContext(applicationContext)
    }

    /**
     * jwt服务类
     *
     */
    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationService::class)
    fun createJwtService(
        jwtTokenProvider: JwtTokenProvider,
        cacheService: CacheService,
        jwtProperties: JwtProperties
    ): JwtAuthenticationService {
        log.info("创建默认jwt认证服务实现...")
        return JwtAuthenticationServiceImpl(jwtTokenProvider, cacheService, jwtProperties)
    }

    /**
     * Jwt令牌提供程序
     */
    @Bean
    @ConditionalOnMissingBean(JwtTokenProvider::class)
    fun createJwtTokenProvider(jwtProperties: JwtProperties, cacheService: CacheService): JwtTokenProvider {
        log.info("创建默认jwt令牌提供程序实现...")
        return DefaultJwtTokenProvider(jwtProperties, cacheService)
    }

    /**
     * 用户信息服务
     *
     * @throws InternalException 内部异常
     */
    @Bean("userDetailsService")
    @ConditionalOnMissingBean(UserDetailsService::class)
    fun createUserDetailsService(): UserDetailsService {
        throw InternalException("请实现bean名称为userDetailsService的UserDetailsService接口用于提供基本的用户名密码验证服务!")
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
        @Qualifier("i18nMessageSource") i18nMessageSource: MessageSource
    ): AuthenticationSuccessHandler {
        log.info("创建默认登录成功处理器...")
        return LoginSuccessJsonResponseHandler(
            objectMapper,
            jwtAuthenticationService,
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
        log.info("创建登录失败处理器...")
        return LoginFailJsonResponseHandler(objectMapper, i18nMessageSource)
    }

    /**
     * 创建登录认证过滤器 用于处理默认的用户名密码登录请求
     *
     * @param properties                   安全认证配置
     * @param authenticationProviders       认证提供器
     * @param authenticationSuccessHandler 认证成功处理器
     * @param authenticationFailureHandler 认证失败处理器
     * @param objectMapper                 json对象映射器
     * @return 登录认证过滤器
     */
    @Bean("defaultAuthenticationFilter")
    @ConditionalOnMissingBean(DefaultAuthenticationFilter::class)
    fun createDefaultAuthenticationFilter(
        properties: SecurityAuthenticationProperties,
        authenticationProviders: List<AbstractPrincipalPasswordAuthenticationProvider>,
        authenticationSuccessHandler: AuthenticationSuccessHandler,
        authenticationFailureHandler: AuthenticationFailureHandler,
        objectMapper: ObjectMapper,
        loginRequestParserFactory: LoginRequestParserFactory,
        loginRequestConverterContext: LoginRequestConverterContext
    ): DefaultAuthenticationFilter {
        log.info("创建默认登录认证过滤器...")
        return DefaultAuthenticationFilter(
            AntPathRequestMatcher(
                properties.baseUrl+properties.loginUrl,
                HttpMethod.POST.name()
            ),
            ProviderManager(
                authenticationProviders
            ),
            authenticationSuccessHandler,
            authenticationFailureHandler,
            objectMapper,
            loginRequestParserFactory,
            loginRequestConverterContext
        )
    }

    /**
     * 用户信息缓存 默认为空实现
     *
     * @return 用户信息缓存
     */
    @Bean
    @ConditionalOnMissingBean(UserCacheService::class)
    fun createUserCache(): UserCacheService {
        log.info("创建默认用户信息缓存实现...")
        return NullUserCacheService()
    }

    /**
     * 用户权限映射 默认为空实现
     *
     * @return 用户权限映射器
     */
    @Bean
    @ConditionalOnMissingBean(GrantedAuthoritiesMapper::class)
    fun createGrantedAuthoritiesMapper(): GrantedAuthoritiesMapper {
        log.info("创建默认用户权限映射器实现...")
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
        log.info("创建默认的密码加密器 BCryptPasswordEncoder...")
        return BCryptPasswordEncoder()
    }

    /**
     * 用户名密码登录认证提供器
     *
     * @param userCacheService                        用户信息缓存服务
     * @param authoritiesMapper                权限映射器
     * @param passwordEncoder                  密码加密器
     * @param userDetailsService 用户信息服务
     * @return 用户名密码登录认证提供器
     */
    @Bean
    fun createUsernamePasswordAuthenticationProvider(
        userCacheService: UserCacheService,
        authoritiesMapper: GrantedAuthoritiesMapper,
        passwordEncoder: PasswordEncoder,
        @Qualifier("userDetailsService")
        userDetailsService: UserDetailsService
    ): UsernamePasswordAuthenticationProvider {
        log.info("创建默认用户名密码登录认证提供器...")
        return UsernamePasswordAuthenticationProvider(
            userCacheService,
            authoritiesMapper,
            passwordEncoder,
            userDetailsService
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
        log.info("创建默认认证异常处理器...")
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
        log.info("创建默认登出成功处理器...")
        return LogoutSuccessJsonResponseHandler(objectMapper, jwtAuthenticationService, i18nMessageSource)
    }
}
