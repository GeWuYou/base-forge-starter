package com.gewuyou.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.web.entity.Result;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * 登录失败处理程序
 *
 * @author gewuyou
 * @since 2024-11-24 20:09:43
 */
public class LoginFailHandler implements AuthenticationFailureHandler {
    private final ObjectMapper objectMapper;
    public LoginFailHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        String message = exception.getMessage();
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        PrintWriter writer = response.getWriter();
        writer.print(objectMapper.writeValueAsString(Result.failure(HttpStatus.INTERNAL_SERVER_ERROR.value(),message)));
        writer.flush();
        writer.close();
    }
}
