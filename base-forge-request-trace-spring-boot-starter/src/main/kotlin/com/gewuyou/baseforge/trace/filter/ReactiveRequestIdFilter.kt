package com.gewuyou.baseforge.trace.filter


import com.gewuyou.baseforge.core.constants.WebCommonConstant
import com.gewuyou.baseforge.core.extension.isSkipRequest
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.trace.util.RequestIdUtil
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
        } ?: RequestIdUtil.generateRequestId()
        // 获取当前的 requestId
        val currentRequestId = RequestIdUtil.getRequestId()
        // 将 requestId 设置到日志中
        MDC.put(WebCommonConstant.REQUEST_ID_MDC_KEY, currentRequestId)
        log.info("设置 Request id: $currentRequestId")
        // ✅ **创建新的 request 并更新 exchange**
        val mutatedRequest = request.mutate()
            .header(WebCommonConstant.REQUEST_ID_HEADER, currentRequestId)
            .build()
        val mutatedExchange = exchange.mutate().request(mutatedRequest).build()
        // 放行请求
        return chain.filter(mutatedExchange)
            // ✅ 让 Reactor 线程也能获取 requestId
            .contextWrite { ctx -> ctx.put(WebCommonConstant.REQUEST_ID_MDC_KEY, currentRequestId) }
            .doFinally {
                // 清理 MDC 中的 requestId，避免内存泄漏
                MDC.remove(WebCommonConstant.REQUEST_ID_MDC_KEY)
                // 将 requestId 清除
                RequestIdUtil.removeRequestId()
            }
    }
}