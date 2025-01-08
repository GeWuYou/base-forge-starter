package com.gewuyou.baseforge.core.extension

import com.gewuyou.baseforge.core.constants.WebCommonConstant
import jakarta.servlet.ServletRequest
import jakarta.servlet.http.HttpServletRequest


/**
 *请求扩展类
 *
 * @since 2025-01-02 13:51:08
 * @author gewuyou
 */


/**
 * 从请求属性中获取设备ID
 * @return 设备ID包装类
 */
fun ServletRequest.getDeviceId(): String? {
    return this.getAttribute(WebCommonConstant.DEVICE_ID_ATTRIBUTE) as? String
}

/**
 * 从请求属性中获取请求ID
 * @return 请求ID包装类
 */
fun ServletRequest.getRequestIdByAttribute(): String? {
    return this.getAttribute(WebCommonConstant.REQUEST_ID_ATTRIBUTE) as? String
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