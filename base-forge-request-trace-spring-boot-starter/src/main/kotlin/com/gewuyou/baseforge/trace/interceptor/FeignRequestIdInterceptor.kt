package com.gewuyou.baseforge.trace.interceptor

import com.gewuyou.baseforge.core.constants.WebCommonConstant
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.trace.util.RequestIdUtil
import feign.RequestInterceptor
import feign.RequestTemplate

/**
 * Feign请求ID 拦截器
 *
 * @since 2025-03-17 16:42:50
 * @author gewuyou
 */
class FeignRequestIdInterceptor : RequestInterceptor {
    override fun apply(template: RequestTemplate) {
        // 尝试获取当前请求的请求id
        val requestId = RequestIdUtil.getRequestId()
        requestId?.let {
            // 如果请求id存在，则添加到请求头中
            template.header(WebCommonConstant.REQUEST_ID_HEADER, requestId)
        } ?: run {
            log.warn("请求ID为null，请检查您是否已在过滤链中添加了请求filter。")
        }
    }
}