package com.gewuyou.baseforge.security.authentication.autoconfigure.parser

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.security.authentication.entities.entity.request.DefaultLoginRequest
import com.gewuyou.baseforge.security.authentication.entities.entity.request.LoginRequest
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.MediaType


/**
 *默认登录请求解析器
 * 这个类提供了默认的登录请求解析器，它会从请求中解析请求体并根据请求头中的Content-Type字段来判断请求体的格式，目前仅支持JSON格式的请求体和表单格式的请求体。
 * @since 2025-02-15 02:36:04
 * @author gewuyou
 */

/**
 *JSON 登录请求解析器
 *
 * 解析 JSON 格式的登录请求，生成相应的 LoginRequest 对象。
 */
class JsonLoginRequestParser(
    private val mapper: ObjectMapper
) : LoginRequestParser {
    override fun parse(request: HttpServletRequest): LoginRequest {
        request.inputStream.use {
            return mapper.readValue(it,DefaultLoginRequest::class.java)
        }
    }

    /**
     * 获取支持的请求内容类型
     * @return 请求内容类型
     */
    override fun getSupportedContentType(): String {
        return MediaType.APPLICATION_JSON_VALUE
    }
}

/**
 * Form 请求解析器
 *
 * 解析 Form 格式的登录请求，生成相应的 LoginRequest 对象。
 */
class FormLoginRequestParser : LoginRequestParser {
    override fun parse(request: HttpServletRequest): LoginRequest {
        val principal = request.getParameter("principal") ?: throw IllegalArgumentException("Principal is required")
        val credentials =
            request.getParameter("credentials") ?: throw IllegalArgumentException("Credentials are required")
        val loginType = request.getParameter("loginType") ?: throw IllegalArgumentException("LoginType is required")
        return DefaultLoginRequest(principal, credentials, loginType)
    }

    /**
     * 获取支持的请求内容类型
     * @return 请求内容类型
     */
    override fun getSupportedContentType(): String {
        return MediaType.APPLICATION_FORM_URLENCODED_VALUE
    }
}