package com.gewuyou.baseforge.security.authentication.autoconfigure.interceptor

import org.springframework.web.servlet.HandlerInterceptor

/**
 *幂等登录请求拦截器接口
 *
 * @since 2025-01-06 11:21:25
 * @author gewuyou
 */
interface IdempotentLoginRequestInterceptor : HandlerInterceptor