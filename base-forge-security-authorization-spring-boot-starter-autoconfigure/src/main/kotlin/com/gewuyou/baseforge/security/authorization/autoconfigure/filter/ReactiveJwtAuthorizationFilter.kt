package com.gewuyou.baseforge.security.authorization.autoconfigure.filter

import com.gewuyou.baseforge.core.constants.SecurityAuthenticationCommonConstant
import com.gewuyou.baseforge.core.extension.getAccessToken
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authentication.entities.token.PrincipalCredentialsAuthenticationToken
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.SecurityAuthorizationProperties
import com.gewuyou.baseforge.security.authorization.autoconfigure.service.JwtAuthorizationService
import com.gewuyou.baseforge.security.authorization.entities.exception.AuthorizationException
import com.gewuyou.baseforge.security.authorization.entities.i18n.enums.SecurityAuthorizationResponseInformation
import org.springframework.http.server.PathContainer
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilterChain
import org.springframework.web.util.pattern.PathPatternParser
import reactor.core.publisher.Mono

/**
 *反应式 JWT 授权过滤器
 *
 * @since 2025-02-03 11:50:58
 * @author gewuyou
 */
class ReactiveJwtAuthorizationFilter(
    private val jwtAuthorizationService: JwtAuthorizationService,
    private val authorizationProperties: SecurityAuthorizationProperties // 添加 Security 配置
) : ReactiveAuthorizationFilter {
    private val pathPatternParser = PathPatternParser()
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val requestPath = exchange.request.uri.path
        val pathContainer = PathContainer.parsePath(requestPath) // 将 String 转换为 PathContainer
        // ✅ 先检查 ignored 路径，直接跳过 JWT 过滤
        if (authorizationProperties.ignored.any { pathPatternParser.parse(it).matches(pathContainer) }) {
            log.info("路径 {} 被忽略，跳过 JWT 验证", requestPath)
            return chain.filter(exchange)
        }
        log.info("验证路径：{}", requestPath)
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
                val auth =
                    PrincipalCredentialsAuthenticationToken.authenticated(userDetails, null, userDetails.authorities)
                // 将认证信息放入反应式 SecurityContext 中，并继续过滤链
                chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withAuthentication(auth))
            }
    }
}