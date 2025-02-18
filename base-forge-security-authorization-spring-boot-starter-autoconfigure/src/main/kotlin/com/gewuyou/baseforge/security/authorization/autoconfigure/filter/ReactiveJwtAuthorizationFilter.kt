package com.gewuyou.baseforge.security.authorization.autoconfigure.filter

import com.gewuyou.baseforge.core.constants.SecurityAuthenticationCommonConstant
import com.gewuyou.baseforge.core.extension.getAccessToken
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authentication.entities.token.PrincipalCredentialsAuthenticationToken
import com.gewuyou.baseforge.security.authorization.autoconfigure.service.JwtAuthorizationService
import com.gewuyou.baseforge.security.authorization.entities.exception.AuthorizationException
import com.gewuyou.baseforge.security.authorization.entities.i18n.enums.SecurityAuthorizationResponseInformation
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 *反应式 JWT 授权过滤器
 *
 * @since 2025-02-03 11:50:58
 * @author gewuyou
 */
class ReactiveJwtAuthorizationFilter(
    private val jwtAuthorizationService: JwtAuthorizationService
) : ReactiveAuthorizationFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        log.info("验证路径：{}", exchange.request.uri.path)
        val token = exchange.request.getAccessToken()
            ?: return Mono.error(AuthorizationException(SecurityAuthorizationResponseInformation.MISSING_ACCESS_TOKENS))
        // 验证token
        return Mono.justOrEmpty(jwtAuthorizationService.validateToken(token))
            .switchIfEmpty(Mono.error(AuthorizationException(SecurityAuthorizationResponseInformation.ACCESS_TOKEN_HAS_EXPIRED)))
            .flatMap { jwtClaims ->
                // 确保 jwtClaims 不是 null
                if (jwtClaims == null) {
                    return@flatMap Mono.error<Void>(AuthorizationException(SecurityAuthorizationResponseInformation.ACCESS_TOKEN_HAS_EXPIRED))
                }
                // 从token中获取用户信息
                val userDetails = jwtClaims.claims[SecurityAuthenticationCommonConstant.USER_DETAILS] as UserDetails
                log.info("token 验证通过，用户信息：{}", userDetails)
                // 生成经过认证的 token
                val auth= PrincipalCredentialsAuthenticationToken.authenticated(userDetails,null, userDetails.authorities)
                // 将认证信息放入反应式 SecurityContext 中，并继续过滤链
                chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
            }
    }
}