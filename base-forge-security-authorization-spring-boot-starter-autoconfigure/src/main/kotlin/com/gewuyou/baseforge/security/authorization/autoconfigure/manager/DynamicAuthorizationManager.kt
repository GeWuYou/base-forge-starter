package com.gewuyou.baseforge.security.authorization.autoconfigure.manager

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authorization.autoconfigure.handler.AuthorizationHandler
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.authorization.AuthorizationManager
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import java.util.function.Supplier

/**
 *授权管理器
 *
 * @since 2025-01-07 15:44:33
 * @author gewuyou
 */
class DynamicAuthorizationManager(
   private val authorizationHandler: AuthorizationHandler
) : AuthorizationManager<RequestAuthorizationContext> {
    /**
     * Determines if access is granted for a specific authentication and object.
     * @param authentication the [Supplier] of the [Authentication] to check
     * @param `object` the [RequestAuthorizationContext] object to check
     * @return an [AuthorizationDecision] or null if no decision could be made
     */
    override fun check(
        authentication: Supplier<Authentication>,
        context: RequestAuthorizationContext
    ): AuthorizationDecision {
        val request = context.request
        // 获取请求路径
        val requestURI = request.requestURI
        log.info("DynamicAuthorizationManager 检查请求: $requestURI")
        // 调用授权处理器进行授权
        return authorizationHandler.handle(authentication, context)
    }

}