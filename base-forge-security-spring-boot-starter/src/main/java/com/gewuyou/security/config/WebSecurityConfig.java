package com.gewuyou.security.config;

import com.gewuyou.security.config.entity.SecurityProperties;
import com.gewuyou.security.filter.JwtAuthorizationFilter;
import com.gewuyou.security.filter.NormalLoginAuthenticationFilter;
import com.gewuyou.security.manager.DynamicAuthorizationManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.savedrequest.NullRequestCache;

/**
 * 安全配置类
 *
 * @author gewuyou
 * @since 2024-11-03 15:43:00
 */
@Configuration
@EnableAutoConfiguration
@EnableWebSecurity
public class WebSecurityConfig {
    private final NormalLoginAuthenticationFilter normalLoginAuthenticationFilter;
    private final DynamicAuthorizationManager dynamicAuthorizationManager;
    private final JwtAuthorizationFilter jwtAuthorizationFilter;

    @Autowired
    public WebSecurityConfig(
            NormalLoginAuthenticationFilter normalLoginAuthenticationFilter,
            DynamicAuthorizationManager dynamicAuthorizationManager,
            JwtAuthorizationFilter jwtAuthorizationFilter
    ) {
        this.normalLoginAuthenticationFilter = normalLoginAuthenticationFilter;
        this.dynamicAuthorizationManager = dynamicAuthorizationManager;
        this.jwtAuthorizationFilter = jwtAuthorizationFilter;
    }

    /**
     * 清理不需要的配置
     *
     * @param http HttpSecurity
     * @throws Exception 异常
     */
    private void cleanUnNeedConfig(HttpSecurity http) throws Exception {
        http
                // 关闭csrf
                .csrf(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(AbstractHttpConfigurer::disable)
                // 无需匿名
                .anonymous(AbstractHttpConfigurer::disable)
                // 前后端分离项目无需重定向，requestCache 也用不上
                .requestCache(cache -> cache.requestCache(new NullRequestCache()));
    }

    /**
     * 登录过滤器链
     * @param http HttpSecurity
     * @param securityProperties 安全配置
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean(name = "loginFilterChain")
    public SecurityFilterChain loginFilterChain(HttpSecurity http, SecurityProperties securityProperties) throws Exception {
        cleanUnNeedConfig(http);
        // 使用securityMatcher 匹配用户标识凭证登录请求
        http
                .securityMatcher(securityProperties.getLoginUrlPrefix())
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        .anyRequest()
                                        .authenticated());
        http.addFilterBefore(normalLoginAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    /**
     * 其他过滤器链
     * @param http HttpSecurity
     * @return SecurityFilterChain
     * @throws Exception 异常
     */
    @Bean(name = "otherFilterChain")
    public SecurityFilterChain otherFilterChain(HttpSecurity http) throws Exception {
        cleanUnNeedConfig(http);
        http
                .authorizeHttpRequests(
                        auth ->
                                auth
                                        .anyRequest()
                                        .access(dynamicAuthorizationManager));
        http.addFilterBefore(jwtAuthorizationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

}
