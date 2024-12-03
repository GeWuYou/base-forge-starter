package com.gewuyou.security.config;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 通用安全配置
 *
 * @author gewuyou
 * @since 2024-11-05 18:24:12
 */
@Configuration
@EnableAutoConfiguration
public class CommonSecurityConfig {
    @Bean
    public BCryptPasswordEncoder createBcryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }
    /**
     * 认证管理器
     *
     * @return org.springframework.security.authentication.AuthenticationManager
     * @apiNote
     * @since 2023/3/22 15:53
     */
    @Bean
    public AuthenticationManager createAuthenticationManager(HttpSecurity http) throws Exception {
        return http.getSharedObject(AuthenticationManagerBuilder.class).build();
    }
}
