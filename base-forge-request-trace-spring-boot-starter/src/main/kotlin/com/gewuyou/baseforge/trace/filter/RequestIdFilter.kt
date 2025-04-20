package com.gewuyou.baseforge.trace.filter


import com.gewuyou.baseforge.core.constants.WebCommonConstant
import com.gewuyou.baseforge.core.extension.isSkipRequest
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.trace.util.RequestIdUtil
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
            ) ?:run {
                // 如果没有，则生成新的 requestId
                RequestIdUtil.generateRequestId()
            }
            // 获取 requestId
            val requestId = RequestIdUtil.getRequestId()
            // 将requestId 设置到日志中
            MDC.put(WebCommonConstant.REQUEST_ID_MDC_KEY, requestId)
            log.info("设置 Request id: $requestId")
            // 将 requestId 设置到响应头中
            response.setHeader(WebCommonConstant.REQUEST_ID_HEADER, requestId)
            // 继续处理请求
            chain.doFilter(request, response)
        }
        finally {
            MDC.remove(WebCommonConstant.REQUEST_ID_MDC_KEY)
            // 清理当前线程的 RequestId，防止内存泄漏
            RequestIdUtil.removeRequestId()
        }
    }
}