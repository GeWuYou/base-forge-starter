package com.gewuyou.baseforge.security.authentication.autoconfigure.parser

import com.gewuyou.baseforge.security.authentication.entities.entity.request.LoginRequest
import jakarta.servlet.http.HttpServletRequest

/**
 *请求解析器
 *
 * @since 2025-02-15 02:18:47
 * @author gewuyou
 */
interface LoginRequestParser {
    /**
     * 解析请求
     * @param request 请求对象
     * @return 登录请求对象
     */
    fun parse(request: HttpServletRequest): LoginRequest

    /**
     * 获取支持的请求内容类型
     * @return 请求内容类型
     */
    fun getSupportedContentType(): String
}