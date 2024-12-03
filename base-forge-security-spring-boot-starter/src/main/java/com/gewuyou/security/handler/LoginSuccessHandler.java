package com.gewuyou.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.i18n.enums.ResponseInformation;
import com.gewuyou.security.config.entity.JwtAccessProperties;
import com.gewuyou.security.config.entity.JwtRefreshProperties;
import com.gewuyou.security.dto.BaseUserDetails;
import com.gewuyou.security.entity.JwtProperties;
import com.gewuyou.security.manager.JtiManager;
import com.gewuyou.security.service.IJwtService;
import com.gewuyou.util.SpringUtil;
import com.gewuyou.web.entity.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;
import java.util.UUID;

/**
 * 登录成功处理程序
 *
 * @author gewuyou
 * @since 2024-11-24 16:16:12
 */
public class LoginSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements AuthenticationSuccessHandler {

    private final IJwtService jwtService;
    private final JtiManager jtiManager;
    private final JwtAccessProperties jwtAccessProperties;
    private final JwtRefreshProperties jwtRefreshProperties;
    private final ObjectMapper objectMapper;

    public LoginSuccessHandler(
            IJwtService jwtService,
            JtiManager jtiManager,
            JwtAccessProperties jwtAccessProperties,
            JwtRefreshProperties jwtRefreshProperties,
            ObjectMapper objectMapper
    ) {
        this.jwtService = jwtService;
        this.jtiManager = jtiManager;
        this.jwtAccessProperties = jwtAccessProperties;
        this.jwtRefreshProperties = jwtRefreshProperties;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Object principal = authentication.getPrincipal();
        if (principal instanceof BaseUserDetails userDetails) {
            // 生成访问jti
            String accessJti = UUID.randomUUID().toString().replaceAll("-", "");
            // 生成刷新jti
            String refreshJti = UUID.randomUUID().toString().replaceAll("-", "");
            String userAuthId = userDetails.getUserAuthId();
            // 缓存jti
            jtiManager.addJti(userAuthId,accessJti, jwtAccessProperties.getExpiration());
            jtiManager.addJti(userAuthId,refreshJti, jwtRefreshProperties.getExpiration());
            String accessToken =jwtService.generateAccessToken(JwtProperties
                    .builder()
                    .userDetails(userDetails)
                    .userAuthId(userAuthId)
                    .jti(accessJti)
                    .build());
            String refreshToken = jwtService.generateRefreshToken(JwtProperties
                    .builder()
                    .jti(refreshJti)
                    .userAuthId(userAuthId)
                    .build());
            Object details = authentication.getDetails();
            Map<String, Object> result = Map.of(
                    "access_token", accessToken,
                    "refresh_token", refreshToken,
                    "details", details
            );
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            PrintWriter writer = response.getWriter();
            writer.print(objectMapper.writeValueAsString(Result.success(ResponseInformation.LOGIN_SUCCESS, result, SpringUtil.getBean(MessageSource.class))));
            writer.flush();
            writer.close();
        } else {
            throw new IllegalArgumentException("principal is not UserDetails");
        }
    }
}
