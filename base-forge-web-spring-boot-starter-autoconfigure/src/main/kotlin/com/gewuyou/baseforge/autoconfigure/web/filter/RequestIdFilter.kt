package com.gewuyou.baseforge.autoconfigure.web.filter

import com.gewuyou.baseforge.autoconfigure.util.RequestIdUtil
import com.gewuyou.baseforge.core.constants.WebCommonConstant
import com.gewuyou.baseforge.core.extension.isSkipRequest
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.MDC
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

/**
 *请求过滤器
 *
 * @since 2025-01-02 14:31:07
 * @author gewuyou
 */
@Component
class RequestIdFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain
    ) {
        // 跳过OPTIONS请求
        if (request.isSkipRequest()) {
            return chain.doFilter(request, response)
        }
        try {
            // 尝试从请求头中获取 requestId
            request.getHeader(WebCommonConstant.REQUEST_ID_HEADER)?.also(
                RequestIdUtil::setRequestId
            )?:
                // 如果没有，则生成新的 requestId
                RequestIdUtil.generateRequestId()

            // 获取 requestId
            val requestId = RequestIdUtil.getRequestId()
            // 将requestId 设置到日志中
            MDC.put(WebCommonConstant.REQUEST_ID_MDC_KEY, requestId)
            // 继续处理请求
            chain.doFilter(request, response)
        } finally {
            // 将 requestId 设置到响应头中
            response.setHeader(WebCommonConstant.REQUEST_ID_HEADER, RequestIdUtil.getRequestId())
            // 清理当前线程的 RequestId，防止内存泄漏
            RequestIdUtil.removeRequestId()
            MDC.remove(WebCommonConstant.REQUEST_ID_MDC_KEY)
        }
    }
}