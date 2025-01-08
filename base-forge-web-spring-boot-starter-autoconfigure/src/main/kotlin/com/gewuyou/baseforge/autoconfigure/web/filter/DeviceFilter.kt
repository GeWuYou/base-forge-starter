package com.gewuyou.baseforge.autoconfigure.web.filter

import com.gewuyou.baseforge.core.constants.WebCommonConstant
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.entities.web.exception.AccessException
import com.gewuyou.baseforge.entities.web.i18n.enums.WebResponseInformation
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import org.apache.commons.lang3.StringUtils
import java.io.IOException


/**
 * 登录设备过滤器
 *
 * @author gewuyou
 * @since 2025-01-01 23:45:39
 */
class DeviceFilter : Filter {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        val deviceId = httpRequest.getHeader(WebCommonConstant.DEVICE_ID_HEADER)
        if (StringUtils.isEmpty(deviceId)) {
            log.warn("Device ID not provided in request from IP: {}", httpRequest.remoteAddr)
            throw AccessException(WebResponseInformation.DEVICE_ID_NOT_PROVIDED)
        }
        // 将 deviceId 存入请求上下文
        httpRequest.setAttribute(WebCommonConstant.DEVICE_ID_ATTRIBUTE, deviceId)
        chain.doFilter(request, response)
    }
}
