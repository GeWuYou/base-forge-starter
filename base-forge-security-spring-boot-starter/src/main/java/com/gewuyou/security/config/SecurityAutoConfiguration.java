package com.gewuyou.security.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.redis.service.ICacheService;
import com.gewuyou.security.checker.DefaultPostAuthenticationChecks;
import com.gewuyou.security.checker.DefaultPreAuthenticationChecks;
import com.gewuyou.security.checker.UserDetailsChecker;
import com.gewuyou.security.config.entity.JwtAccessProperties;
import com.gewuyou.security.config.entity.JwtRefreshProperties;
import com.gewuyou.security.config.entity.SecurityProperties;
import com.gewuyou.security.filter.JwtAuthorizationFilter;
import com.gewuyou.security.filter.NormalLoginAuthenticationFilter;
import com.gewuyou.security.handler.LoginFailHandler;
import com.gewuyou.security.handler.LoginSuccessHandler;
import com.gewuyou.security.manager.CacheJtiManager;
import com.gewuyou.security.manager.DynamicAuthorizationManager;
import com.gewuyou.security.manager.JtiManager;
import com.gewuyou.security.manager.MemoryJtiManager;
import com.gewuyou.security.provider.NormalLoginAuthenticationProvider;
import com.gewuyou.security.service.IDynamicSecurityService;
import com.gewuyou.security.service.IJwtService;
import com.gewuyou.security.service.IUserDetailsService;
import com.gewuyou.security.service.impl.DefaultDynamicSecurityService;
import com.gewuyou.security.service.impl.DefaultUserDetailsService;
import com.gewuyou.security.service.impl.JwtServiceImpl;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.cache.NullUserCache;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 * 安全自动配置
 *
 * @author gewuyou
 * @since 2024-11-23 17:48:41
 */
@EnableAutoConfiguration
@Configuration
@EnableConfigurationProperties({SecurityProperties.class, JwtAccessProperties.class, JwtRefreshProperties.class})
public class SecurityAutoConfiguration {
    /**
     * 前置认证检查器bean名称
     */
    public static final String PRE_AUTHENTICATION_CHECKER_BEAN_NAME = "preAuthenticationChecker";

    /**
     * 后置认证检查器bean名称
     */
    public static final String POST_AUTHENTICATION_CHECKER_BEAN_NAME = "postAuthenticationChecker";


    /**
     * 普通登录认证提供者
     *
     * @param passwordEncoder    密码加密器
     * @param userDetailsService 用户详情服务
     * @return 普通登录认证提供者
     */
    @Bean
    public NormalLoginAuthenticationProvider createNormalLoginAuthenticationProvider(
            @Qualifier(PRE_AUTHENTICATION_CHECKER_BEAN_NAME)
            UserDetailsChecker preAuthenticationChecker,
            @Qualifier(POST_AUTHENTICATION_CHECKER_BEAN_NAME)
            UserDetailsChecker postAuthenticationChecker,
            UserCache userCache,
            PasswordEncoder passwordEncoder,
            IUserDetailsService userDetailsService
    ) {
        return new NormalLoginAuthenticationProvider(
                preAuthenticationChecker,
                postAuthenticationChecker,
                userCache,
                passwordEncoder, userDetailsService);
    }

    /**
     * 用户详情服务
     *
     * @return 用户详情服务
     */
    @Bean
    @ConditionalOnMissingBean(IUserDetailsService.class)
    public IUserDetailsService createUserDetailsService() {
        return new DefaultUserDetailsService();
    }

    /**
     * 普通登录认证过滤器
     *
     * @param securityProperties           安全配置属性
     * @param authenticationManager        认证管理器
     * @param authenticationSuccessHandler 认证成功处理器
     * @param authenticationFailureHandler 认证失败处理器
     * @param objectMapper                 objectMapper
     * @return 普通登录认证过滤器
     */
    @Bean
    public NormalLoginAuthenticationFilter createNormalLoginAuthenticationFilter(
            SecurityProperties securityProperties,
            AuthenticationManager authenticationManager,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            ObjectMapper objectMapper
    ) {
        return new NormalLoginAuthenticationFilter(
                new AntPathRequestMatcher(
                        securityProperties.getNormalLoginUrl(),
                        HttpMethod.POST.name()
                ),
                authenticationManager,
                authenticationSuccessHandler,
                authenticationFailureHandler,
                objectMapper
        );
    }

    /**
     * 前置认证检查器
     *
     * @return 前置认证检查器
     */
    @Bean(name = PRE_AUTHENTICATION_CHECKER_BEAN_NAME)
    @ConditionalOnMissingBean(name = PRE_AUTHENTICATION_CHECKER_BEAN_NAME)
    public UserDetailsChecker createPreAuthenticationChecker() {
        return new DefaultPreAuthenticationChecks();
    }

    /**
     * 后置认证检查器
     *
     * @return 后置认证检查器
     */
    @Bean(name = POST_AUTHENTICATION_CHECKER_BEAN_NAME)
    @ConditionalOnMissingBean(name = POST_AUTHENTICATION_CHECKER_BEAN_NAME)
    public UserDetailsChecker createPostAuthenticationChecker() {
        return new DefaultPostAuthenticationChecks();
    }

    /**
     * jwt服务类
     *
     * @param jwtAccessProperties  jwt访问令牌配置属性
     * @param jwtRefreshProperties jwt刷新令牌配置属性
     * @param jtiManager           jti状态提供者
     * @return jwt服务类
     */
    @Bean
    @ConditionalOnMissingBean(IJwtService.class)
    public IJwtService createJwtService(
            JwtAccessProperties jwtAccessProperties,
            JwtRefreshProperties jwtRefreshProperties,
            JtiManager jtiManager
    ) {
        return new JwtServiceImpl(jwtAccessProperties, jwtRefreshProperties, jtiManager);
    }

    /**
     * 登录成功处理器
     *
     * @param jwtService          jwt服务
     * @param jtiManager          jti管理器
     * @param jwtAccessProperties jwt配置属性
     * @param objectMapper        objectMapper
     * @return 登录成功处理器
     */
    @Bean
    public LoginSuccessHandler createLoginSuccessHandler(
            IJwtService jwtService,
            JtiManager jtiManager,
            JwtAccessProperties jwtAccessProperties,
            JwtRefreshProperties jwtRefreshProperties,
            ObjectMapper objectMapper
    ) {
        return new LoginSuccessHandler(jwtService, jtiManager, jwtAccessProperties, jwtRefreshProperties, objectMapper);
    }

    /**
     * 登录失败处理器
     *
     * @param objectMapper objectMapper
     * @return 登录失败处理器
     */
    @Bean
    public LoginFailHandler createLoginFailHandler(ObjectMapper objectMapper) {
        return new LoginFailHandler(objectMapper);
    }

    /**
     * 动态权限管理器
     *
     * @param dynamicSecurityService 动态权限服务
     * @return 动态权限管理器
     */
    @Bean
    public DynamicAuthorizationManager createDynamicAuthorizationManager(IDynamicSecurityService dynamicSecurityService) {
        return new DynamicAuthorizationManager(dynamicSecurityService);
    }

    /**
     * 动态权限服务
     *
     * @return 动态权限服务
     */
    @Bean
    @ConditionalOnMissingBean(IDynamicSecurityService.class)
    public IDynamicSecurityService createDynamicSecurityService() {
        return new DefaultDynamicSecurityService();
    }

    /**
     * 用户信息缓存
     *
     * @return 用户信息缓存
     */
    @Bean
    @ConditionalOnMissingBean(UserCache.class)
    public UserCache createUserCache() {
        return new NullUserCache();
    }

    /**
     * JWT授权过滤器
     *
     * @param authenticationManager 认证管理器
     * @param jwtService            jwt服务
     * @return JWT授权过滤器
     */
    @Bean
    public JwtAuthorizationFilter createJwtAuthorizationFilter(
            AuthenticationManager authenticationManager,
            IJwtService jwtService,
            ObjectMapper objectMapper
    ) {
        return new JwtAuthorizationFilter(authenticationManager, jwtService, objectMapper);
    }

    /**
     * Jti管理器
     *
     * @return Jti管理器
     */
    @Bean
    @ConditionalOnMissingBean(JtiManager.class)
    public JtiManager createJtiManager(ICacheService cacheService) {
        return new CacheJtiManager(cacheService);
    }

    /**
     * Jti管理器
     *
     * @return Jti管理器
     */
    @Bean
    @ConditionalOnMissingBean(CacheJtiManager.class)
    public JtiManager createJtiManager() {
        return new MemoryJtiManager();
    }
}
