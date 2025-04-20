package com.gewuyou.baseforge.security.authorization.autoconfigure.manager

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authorization.autoconfigure.handler.ReactiveAuthorizationHandler
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.ReactiveAuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authorization.AuthorizationContext
import reactor.core.publisher.Mono

/**
 *反应式动态授权管理器
 *
 * @since 2025-02-03 11:16:39
 * @author gewuyou
 */
class ReactiveDynamicAuthorizationManager(
    private val reactiveAuthorizationHandler: ReactiveAuthorizationHandler
): ReactiveAuthorizationManager<AuthorizationContext> {
    /**
     * Determines if access is granted for a specific authentication and object.
     * @param authentication the Authentication to check
     * @param context the context of the request
     * @return an decision or empty Mono if no decision could be made.
     */
    override fun check(
        authentication: Mono<Authentication>,
        context: AuthorizationContext
    ): Mono<AuthorizationDecision> {
        val request = context.exchange.request
        // 获取请求路径
        val requestPath = request.uri.path
        log.info("ReactiveDynamicAuthorizationManager 检查请求: $requestPath")
        return reactiveAuthorizationHandler.handle(authentication, context)
    }
}