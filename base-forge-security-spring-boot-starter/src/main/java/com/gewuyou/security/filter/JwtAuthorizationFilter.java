package com.gewuyou.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.core.constants.SecurityConstant;
import com.gewuyou.security.enums.TokenType;
import com.gewuyou.security.exception.AuthenticationException;
import com.gewuyou.security.i18n.enums.SecurityResponseInformation;
import com.gewuyou.security.service.IJwtService;
import com.gewuyou.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * JWT 授权过滤器
 *
 * @author gewuyou
 * @since 2024-11-25 17:18:14
 */
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    public final ObjectMapper objectMapper;
    private final IJwtService jwtService;

    public JwtAuthorizationFilter(
            AuthenticationManager authenticationManager,
            IJwtService jwtService,
            ObjectMapper objectMapper
    ) {
        super(authenticationManager);
        this.jwtService = jwtService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("验证路径：{}", request.getRequestURI());
        // 获取token
        String accessToken = JwtUtil.getAccessToken(request);
        if (Objects.isNull(accessToken)) {
            log.debug("未获取到token");
            throw new AuthenticationException(SecurityResponseInformation.LOGIN_IS_INVALID);
        }
        // 验证token
        if (!jwtService.validateToken(accessToken, TokenType.Access)) {
            log.debug("token 验证失败");
            throw new AuthenticationException(SecurityResponseInformation.LOGIN_IS_INVALID);
        }
        String secretKey = jwtService.getJwtProperties(TokenType.Access).getSecretKey();
        // 从token中获取用户信息
        UserDetails userDetails = JwtUtil.getUserDetailsByAccessToken(accessToken, secretKey);
        log.debug("用户信息：{}", userDetails);
        // 设置用户信息到请求头中
        response.setHeader(SecurityConstant.USER_DETAILS, objectMapper.writeValueAsString(userDetails));
        // UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        // SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }
}
