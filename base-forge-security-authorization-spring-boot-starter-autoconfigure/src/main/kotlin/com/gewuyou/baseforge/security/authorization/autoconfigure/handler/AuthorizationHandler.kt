package com.gewuyou.baseforge.security.authorization.autoconfigure.handler

import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.core.Authentication
import org.springframework.security.web.access.intercept.RequestAuthorizationContext
import java.util.function.Supplier

/**
 *授权处理程序
 *
 * @since 2025-01-07 17:19:24
 * @author gewuyou
 */
fun interface AuthorizationHandler {
    /**
     * 处理授权
     * @param authentication 认证信息
     * @param context 请求授权上下文
     * @return 授权决策
     */
    fun handle(authentication: Supplier<Authentication>, context: RequestAuthorizationContext): AuthorizationDecision
}