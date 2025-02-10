package com.gewuyou.baseforge.core.extension

import com.gewuyou.baseforge.core.constants.WebCommonConstant
import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpMethod
import org.springframework.http.server.reactive.ServerHttpRequest


/**
 *请求扩展类
 *
 * @since 2025-01-02 13:51:08
 * @author gewuyou
 */


/**
 * 从请求头中获取设备ID
 * @return 设备ID
 */
fun HttpServletRequest.getDeviceId(): String? {
    return this.getHeader(WebCommonConstant.DEVICE_ID_HEADER)
}

/**
 * 从请求中获取json body
 */
fun ServletRequest.getJsonBody(): String {
    return this.reader.use { it.readText() }
}

/**
 * 从请求头中获取access token
 */
fun HttpServletRequest.getAccessToken(): String? {
    return this.getHeader("Authorization")?.removePrefix("Bearer ")
}

/**
 * 从请求头中获取access token
 */
fun ServerHttpRequest.getAccessToken(): String? {
    return this.headers.getFirst("Authorization")?.removePrefix("Bearer ")
}

/**
 * 判断是否跳过请求
 * @apiNote 这个方法是给请求id过滤器使用的
 * @return true: 跳过请求; false: 不跳过请求
 */
fun HttpServletRequest.isSkipRequest(): Boolean {
    return isSkipRequest(this.method, this.requestURI)
}

/**
 * 判断是否跳过请求
 * @apiNote 这个方法是给反应式请求id过滤器使用的
 * @return true: 跳过请求; false: 不跳过请求
 */
fun ServerHttpRequest.isSkipRequest(): Boolean {
    return isSkipRequest(this.method.name(), this.uri.path)
}

/**
 * 判断是否跳过请求
 * @param method 请求方法
 * @param uri 请求路径
 * @return true: 跳过请求; false: 不跳过请求
 */
fun isSkipRequest(method: String, uri: String): Boolean {
    return when {
        // 跳过 OPTIONS 请求
        HttpMethod.OPTIONS.name() == method -> true
        // 跳过静态资源请求
        HttpMethod.GET.name() == method && uri.matches(Regex(".*\\.(css|js|png|jpg|jpeg|gif|svg)")) -> true
        // 跳过 HEAD 请求
        HttpMethod.HEAD.name() == method -> true
        // 跳过 TRACE 请求
        HttpMethod.TRACE.name() == method -> true
        // 跳过健康检查请求
        uri.startsWith("/actuator/health") || uri == "/health" -> true
        else -> false
    }
}