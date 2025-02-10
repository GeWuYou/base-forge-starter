package com.gewuyou.baseforge.security.authorization.autoconfigure.filter

import com.gewuyou.baseforge.autoconfigure.util.RequestIdUtil
import com.gewuyou.baseforge.core.constants.WebCommonConstant
import com.gewuyou.baseforge.core.extension.isSkipRequest
import org.slf4j.MDC
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono

/**
 *反应式请求 ID 过滤器
 *
 * @since 2025-02-09 02:14:49
 * @author gewuyou
 */
class ReactiveRequestIdFilter : WebFilter {
    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val request = exchange.request
        // 检测请求是否需要跳过
        if (request.isSkipRequest()) {
            return chain.filter(exchange)
        }
        request.headers[WebCommonConstant.REQUEST_ID_HEADER]?.let {
            // 如果请求头中已有 requestId，直接设置到 RequestIdUtil 中
            it.firstOrNull()?.let(RequestIdUtil::setRequestId) ?: RequestIdUtil.generateRequestId()
                .also {
                    val requestId = RequestIdUtil.getRequestId()
                    request.mutate()
                        // 将 requestId 添加到请求头
                        .header(WebCommonConstant.REQUEST_ID_HEADER, requestId)
                        .build()
                }
        }?:run{
            RequestIdUtil.generateRequestId()
            request.mutate()
                // 将 requestId 添加到请求头
                .header(WebCommonConstant.REQUEST_ID_HEADER, RequestIdUtil.getRequestId())
                .build()
        }
        // 获取当前的 requestId
        val currentRequestId = RequestIdUtil.getRequestId()
        // 将 requestId 设置到日志中
        MDC.put(WebCommonConstant.REQUEST_ID_MDC_KEY, currentRequestId)
        // 转发请求
        return chain.filter(exchange)
            .doFinally {
                // 清理 MDC 中的 requestId，避免内存泄漏
                MDC.remove(WebCommonConstant.REQUEST_ID_MDC_KEY)
                // 将 requestId 清除
                RequestIdUtil.removeRequestId()
            }
    }
}