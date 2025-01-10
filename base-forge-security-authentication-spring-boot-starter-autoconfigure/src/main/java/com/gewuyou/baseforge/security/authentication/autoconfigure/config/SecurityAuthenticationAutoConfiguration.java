package com.gewuyou.baseforge.security.authentication.autoconfigure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.core.exception.InternalException;
import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.SecurityAuthenticationProperties;
import com.gewuyou.baseforge.security.authentication.autoconfigure.filter.NormalAuthenticationFilter;
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.AuthenticationExceptionHandler;
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LoginFailJsonResponseHandler;
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LoginSuccessJsonResponseHandler;
import com.gewuyou.baseforge.security.authentication.autoconfigure.handler.LogoutSuccessJsonResponseHandler;
import com.gewuyou.baseforge.security.authentication.autoconfigure.provider.NormalAuthenticationProvider;
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.AuthenticationUserDetailsService;
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.JwtAuthenticationService;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.util.List;

/**
 * 安全认证 自动配置
 *
 * @author gewuyou
 * @since 2025-01-03 10:17:19
 */
@EnableAutoConfiguration
@Configuration
@EnableConfigurationProperties(SecurityAuthenticationProperties.class)
public class SecurityAuthenticationAutoConfiguration {

    /**
     * jwt服务类
     *
     * @throws InternalException 内部异常
     */
    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationService.class)
    public JwtAuthenticationService createJwtService() {
        throw new InternalException("请实现JwtService接口用于提供jwt服务!");
    }

    /**
     * 用户信息服务
     *
     * @throws InternalException 内部异常
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationUserDetailsService.class)
    public AuthenticationUserDetailsService createUserDetailsService() {
        throw new InternalException("请实现UserDetailsService接口用于提供用户信息服务!");
    }

    /**
     * 登录成功处理器
     *
     * @param objectMapper objectMapper
     * @return 登录成功处理器
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationSuccessHandler.class)
    public AuthenticationSuccessHandler createLoginSuccessHandler(
            ObjectMapper objectMapper,
            JwtAuthenticationService jwtAuthenticationService,
            AuthenticationUserDetailsService authenticationUserDetailsService,
            MessageSource i18nMessageSource
    ) {
        return new LoginSuccessJsonResponseHandler(objectMapper, jwtAuthenticationService, authenticationUserDetailsService, i18nMessageSource);
    }

    /**
     * 登录失败处理器
     *
     * @param objectMapper objectMapper
     * @return 登录失败处理器
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationFailureHandler.class)
    public AuthenticationFailureHandler createLoginFailHandler(
            ObjectMapper objectMapper,
            MessageSource i18nMessageSource
    ) {
        return new LoginFailJsonResponseHandler(objectMapper,i18nMessageSource);
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
    public NormalAuthenticationFilter createNormalAuthenticationFilter(
            SecurityAuthenticationProperties properties,
            NormalAuthenticationProvider authenticationProvider,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            ObjectMapper objectMapper
    ) {
        return new NormalAuthenticationFilter(
                new AntPathRequestMatcher(
                        properties.getNormalLoginUrl(),
                        HttpMethod.POST.name()
                ),
                new ProviderManager(
                        List.of(authenticationProvider)
                ),
                authenticationSuccessHandler,
                authenticationFailureHandler,
                objectMapper
        );
    }

    /**
     * 用户信息缓存 默认为空实现
     *
     * @return 用户信息缓存
     */
    @Bean
    @ConditionalOnMissingBean(UserCache.class)
    public UserCache createUserCache() {
        return new NullUserCache();
    }

    /**
     * 用户权限映射 默认为空实现
     *
     * @return 用户权限映射器
     */
    @Bean
    @ConditionalOnMissingBean(GrantedAuthoritiesMapper.class)
    public GrantedAuthoritiesMapper createGrantedAuthoritiesMapper() {
        return new NullAuthoritiesMapper();
    }

    /**
     * 默认的密码加密器为BCryptPasswordEncoder
     *
     * @return 密码加密器
     */
    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder createPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 普通登录认证提供器
     *
     * @param userCache          用户信息缓存
     * @param authoritiesMapper  权限映射器
     * @param passwordEncoder    密码加密器
     * @param authenticationUserDetailsService 用户信息服务
     * @return 普通登录认证提供器
     */
    @Bean
    public NormalAuthenticationProvider createNormalAuthenticationProvider(
            UserCache userCache,
            GrantedAuthoritiesMapper authoritiesMapper,
            PasswordEncoder passwordEncoder,
            AuthenticationUserDetailsService authenticationUserDetailsService) {
        return new NormalAuthenticationProvider(userCache, authoritiesMapper, passwordEncoder, authenticationUserDetailsService);
    }

    /**
     * 认证异常处理器
     *
     * @param objectMapper objectMapper
     * @return 认证异常处理器
     */
    @Bean
    @ConditionalOnMissingBean(AuthenticationEntryPoint.class)
    public AuthenticationEntryPoint createAuthenticationEntryPoint(
            ObjectMapper objectMapper,
            MessageSource i18nMessageSource
    ) {
        return new AuthenticationExceptionHandler(objectMapper, i18nMessageSource);
    }

    /**
     * 登出成功处理器
     *
     * @param objectMapper             json对象映射器
     * @param jwtAuthenticationService jwt服务
     * @return 登出成功处理器
     */
    @Bean
    @ConditionalOnMissingBean(LogoutSuccessHandler.class)
    public LogoutSuccessHandler createLogoutSuccessHandler(ObjectMapper objectMapper,
                                                           JwtAuthenticationService jwtAuthenticationService,
                                                           MessageSource i18nMessageSource
    ) {
        return new LogoutSuccessJsonResponseHandler(objectMapper, jwtAuthenticationService, i18nMessageSource);
    }
}
