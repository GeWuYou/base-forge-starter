package com.gewuyou.baseforge.autoconfigure.web.filter

import com.gewuyou.baseforge.core.constants.WebCommonConstant
import com.gewuyou.baseforge.entities.web.util.RequestIdUtil
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC

/**
 *请求过滤器
 *
 * @since 2025-01-02 14:31:07
 * @author gewuyou
 */
class RequestFilter : Filter {
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        val httpRequest = request as HttpServletRequest
        try {
            // 尝试从请求头中获取 requestId
            httpRequest.getHeader(WebCommonConstant.REQUEST_ID_HEADER)?.also {
                // 如果有，则设置到 RequestIdUtil 中
                RequestIdUtil.setRequestId(it)
            } ?: RequestIdUtil.generateRequestId().also {
                // 如果没有，则生成新的 requestId
                httpRequest.setAttribute(WebCommonConstant.REQUEST_ID_ATTRIBUTE, it)
            }
            // 获取 requestId
            val requestId = RequestIdUtil.getRequestId()
            // 将 requestId 设置到请求属性中
            httpRequest.setAttribute(WebCommonConstant.REQUEST_ID_ATTRIBUTE, requestId)
            // 将requestId 设置到日志中
            MDC.put(WebCommonConstant.REQUEST_ID_MDC_KEY, requestId)
            // 继续处理请求
            chain.doFilter(request, response)
        } finally {
            // 将 requestId 设置到响应头中
            val httpResponse = response as HttpServletResponse
            httpResponse.setHeader(WebCommonConstant.REQUEST_ID_HEADER, RequestIdUtil.getRequestId())
            // 清理当前线程的 RequestId，防止内存泄漏
            RequestIdUtil.removeRequestId()
            MDC.remove(WebCommonConstant.REQUEST_ID_MDC_KEY)
        }
    }
}