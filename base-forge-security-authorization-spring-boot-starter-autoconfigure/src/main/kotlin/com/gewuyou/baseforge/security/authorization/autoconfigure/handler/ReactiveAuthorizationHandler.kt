package com.gewuyou.baseforge.security.authorization.autoconfigure.handler

import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono

/**
 *反应式授权处理程序
 *
 * @since 2025-02-03 11:40:49
 * @author gewuyou
 */
fun interface ReactiveAuthorizationHandler {
    /**
     * 处理授权
     * @param authentication 认证信息
     * @param context 授权上下文
     * @return 授权决策
     */
    fun handle(
        authentication: Mono<Authentication>,
        context: AuthorizationContext
    ): Mono<AuthorizationDecision>
}