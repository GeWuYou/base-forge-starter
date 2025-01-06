package com.gewuyou.baseforge.security.authentication.autoconfigure.config;

import com.gewuyou.baseforge.security.authentication.autoconfigure.config.entity.SecurityAuthenticationProperties;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.security.web.savedrequest.NullRequestCache;

import java.util.List;

/**
 * Spring Security 配置
 *
 * @author gewuyou
 * @since 2025-01-03 10:19:48
 */
@Configuration
@EnableAutoConfiguration
public class SpringSecurityConfiguration {
    private final List<AbstractAuthenticationProcessingFilter> filters;
    private final AuthenticationEntryPoint authenticationExceptionHandler;
    private final SecurityAuthenticationProperties securityAuthenticationProperties;
    private final LogoutSuccessHandler logoutSuccessHandler;

    public SpringSecurityConfiguration(List<AbstractAuthenticationProcessingFilter> filters, AuthenticationEntryPoint authenticationExceptionHandler, SecurityAuthenticationProperties securityAuthenticationProperties, LogoutSuccessHandler logoutSuccessHandler) {
        this.filters = filters;
        this.authenticationExceptionHandler = authenticationExceptionHandler;
        this.securityAuthenticationProperties = securityAuthenticationProperties;
        this.logoutSuccessHandler = logoutSuccessHandler;
    }

    /**
     * 清理不需要的配置并配置安全相关的配置
     *
     * @param http HttpSecurity
     * @throws Exception 异常
     */
    private void cleanUnNeedConfig(HttpSecurity http) throws Exception {
        http
                // 关闭不必要的配置
                .csrf(AbstractHttpConfigurer::disable)
                .logout(logout ->
                        logout.logoutSuccessUrl(securityAuthenticationProperties.getLogoutUrl())
                                .logoutSuccessHandler(logoutSuccessHandler))
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                // 无需匿名
                .anonymous(AbstractHttpConfigurer::disable)
                // 前后端分离项目无需重定向，requestCache 也用不上
                .requestCache(cache -> cache.requestCache(new NullRequestCache()))
                // 处理SpringSecurity异常响应结果。响应数据的结构
                .exceptionHandling(
                        exceptionHandling ->
                                // 处理认证异常 这里不处理授权异常是因为这个模块专注于认证，授权异常由授权模块处理
                                exceptionHandling
                                        .authenticationEntryPoint(authenticationExceptionHandler)
                );
    }

    /**
     * 登录过滤器链
     *
     * @param http               HttpSecurity
     * @param securityProperties 安全配置
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean(name = "loginFilterChain")
    public SecurityFilterChain loginFilterChain(HttpSecurity http, SecurityAuthenticationProperties securityProperties) throws Exception {
        cleanUnNeedConfig(http);
        // 使用securityMatcher 匹配用户标识凭证登录请求
        http
                .securityMatcher(securityProperties.getBaseLoginUrl())
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        .anyRequest()
                                        .authenticated());
        // 添加登录过滤器
        for (AbstractAuthenticationProcessingFilter filter : filters) {
            http.addFilterBefore(filter, UsernamePasswordAuthenticationFilter.class);
        }
        return http.build();
    }
}
