package com.gewuyou.baseforge.security.authorization.autoconfigure.filter

import com.gewuyou.baseforge.core.constants.SecurityAuthenticationCommonConstant
import com.gewuyou.baseforge.core.extension.getAccessToken
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authentication.entities.token.PrincipalCredentialsAuthenticationToken
import com.gewuyou.baseforge.security.authorization.autoconfigure.service.JwtAuthorizationService
import com.gewuyou.baseforge.security.authorization.entities.exception.AuthorizationException
import com.gewuyou.baseforge.security.authorization.entities.i18n.enums.SecurityAuthorizationResponseInformation
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.filter.OncePerRequestFilter

/**
 *JWT 授权过滤器
 *
 * @since 2025-01-06 14:54:44
 * @author gewuyou
 */
class JwtAuthorizationFilter(
    private val jwtAuthorizationService: JwtAuthorizationService
) : OncePerRequestFilter(), AuthorizationFilter {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        log.info("验证路径：{}", request.requestURI)
        // 获取token
        val token = request.getAccessToken() ?: run {
            throw AuthorizationException(SecurityAuthorizationResponseInformation.MISSING_ACCESS_TOKENS)
        }
        // 验证token
        jwtAuthorizationService.validateToken(token)
            ?.let {
                // 从token中获取用户信息
                val userDetails = it.claims[SecurityAuthenticationCommonConstant.USER_DETAILS] as UserDetails
                log.info("token 验证通过，用户信息：{}", userDetails)
                // 从token中获取用户信息，放入request中
                request.setAttribute(SecurityAuthenticationCommonConstant.USER_DETAILS, userDetails)
                // 生成token并将用户信息放入security context中
                SecurityContextHolder.getContext().authentication =
                   PrincipalCredentialsAuthenticationToken.authenticated(userDetails,null,userDetails.authorities)
                log.info("token 验证通过，放行")
                filterChain.doFilter(request, response)
            } ?: run {
            throw AuthorizationException(SecurityAuthorizationResponseInformation.ACCESS_TOKEN_HAS_EXPIRED)
        }
    }
}