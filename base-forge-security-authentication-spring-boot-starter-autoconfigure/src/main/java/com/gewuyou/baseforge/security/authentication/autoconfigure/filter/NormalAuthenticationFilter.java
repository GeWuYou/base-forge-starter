package com.gewuyou.baseforge.security.authentication.autoconfigure.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.security.authentication.entities.entity.request.NormalLoginRequest;
import com.gewuyou.baseforge.security.authentication.entities.token.NormalAuthenticationToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;
import java.util.stream.Collectors;

/**
 * 普通身份验证过滤器
 *
 * @author gewuyou
 * @since 2025-01-01 23:19:07
 */
public class NormalAuthenticationFilter extends AbstractAuthenticationProcessingFilter {
    private final ObjectMapper mapper;

    public NormalAuthenticationFilter(
            AntPathRequestMatcher pathRequestMatcher,
            AuthenticationManager authenticationManager,
            AuthenticationSuccessHandler authenticationSuccessHandler,
            AuthenticationFailureHandler authenticationFailureHandler,
            ObjectMapper mapper
    ) {
        super(pathRequestMatcher);
        setAuthenticationManager(authenticationManager);
        setAuthenticationSuccessHandler(authenticationSuccessHandler);
        setAuthenticationFailureHandler(authenticationFailureHandler);
        this.mapper = mapper;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        // 读取请求体中的数据
        String jsonBody = request
                .getReader()
                .lines()
                .collect(
                        Collectors
                                .joining(System.lineSeparator()));
        // 反序列化
        NormalLoginRequest normalLoginRequest = mapper.readValue(jsonBody, NormalLoginRequest.class);
        // 创建未认证的登录令牌
        NormalAuthenticationToken normalAuthenticationToken = NormalAuthenticationToken.unauthenticated(normalLoginRequest.getPrincipal(), normalLoginRequest.getCredentials());
        // 开始登录认证。SpringSecurity会利用 Authentication对象去寻找 AuthenticationProvider进行登录认证
        return this.getAuthenticationManager().authenticate(normalAuthenticationToken);
    }
}
