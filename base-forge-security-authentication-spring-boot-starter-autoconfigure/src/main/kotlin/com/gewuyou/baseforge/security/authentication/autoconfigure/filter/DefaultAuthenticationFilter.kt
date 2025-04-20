package com.gewuyou.baseforge.security.authentication.autoconfigure.filter

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.security.authentication.autoconfigure.context.LoginRequestConverterContext
import com.gewuyou.baseforge.security.authentication.autoconfigure.factory.LoginRequestParserFactory
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.AuthenticationFailureHandler
import org.springframework.security.web.authentication.AuthenticationSuccessHandler
import org.springframework.security.web.util.matcher.AntPathRequestMatcher
import java.io.IOException

/**
 * 身份验证过滤器
 * 默认的身份验证过滤器，用于处理登录请求，并返回登录令牌。
 * @author gewuyou
 * @since 2025-01-01 23:19:07
 */
class DefaultAuthenticationFilter(
    pathRequestMatcher: AntPathRequestMatcher,
    authenticationManager: AuthenticationManager?,
    authenticationSuccessHandler: AuthenticationSuccessHandler?,
    authenticationFailureHandler: AuthenticationFailureHandler?,
    mapper: ObjectMapper,
    private val loginRequestParserFactory: LoginRequestParserFactory,
    private val loginRequestConverterContext: LoginRequestConverterContext
) : AbstractAuthenticationProcessingFilter(pathRequestMatcher) {
    private val mapper: ObjectMapper

    init {
        setAuthenticationManager(authenticationManager)
        setAuthenticationSuccessHandler(authenticationSuccessHandler)
        setAuthenticationFailureHandler(authenticationFailureHandler)
        this.mapper = mapper
    }

    @Throws(
        AuthenticationException::class,
        IOException::class,
        ServletException::class
    )
    override fun attemptAuthentication(request: HttpServletRequest, response: HttpServletResponse): Authentication {
        // 获取合适地请求解析器
        val parser = loginRequestParserFactory.getParser(request)
        // 解析请求并获取 LoginRequest
        val loginRequest = parser.parse(request)
        // 开始登录认证。SpringSecurity会利用 Authentication对象去寻找 AuthenticationProvider进行登录认证
        return authenticationManager.authenticate(loginRequestConverterContext.executeStrategy(loginRequest))
    }
}
