package com.gewuyou.baseforge.trace.decorator

import com.gewuyou.baseforge.core.constants.WebCommonConstant
import org.slf4j.MDC
import org.springframework.core.task.TaskDecorator

/**
 *请求ID任务装饰器
 *
 * @since 2025-03-17 16:57:54
 * @author gewuyou
 */
class RequestIdTaskDecorator : TaskDecorator {
    override fun decorate(task: Runnable): Runnable {
        // 获取主线程 requestId
        val requestId = MDC.get(WebCommonConstant.REQUEST_ID_MDC_KEY)
        return Runnable {
            try {
                MDC.put(WebCommonConstant.REQUEST_ID_MDC_KEY, requestId)
                task.run()
            } finally {
                MDC.remove(WebCommonConstant.REQUEST_ID_MDC_KEY)
            }
        }
    }
}