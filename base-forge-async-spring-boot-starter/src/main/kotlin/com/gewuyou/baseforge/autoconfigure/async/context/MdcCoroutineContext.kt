package com.gewuyou.baseforge.autoconfigure.async.context

import kotlinx.coroutines.ThreadContextElement
import org.slf4j.MDC
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 *MDC 协程上下文
 *
 * @since 2025-01-23 10:52:18
 * @author gewuyou
 */
class MdcCoroutineContext(
    private val contextMap: Map<String, String>? = MDC.getCopyOfContextMap()
) : ThreadContextElement<Map<String, String>?>, AbstractCoroutineContextElement(Key) {
    companion object Key : CoroutineContext.Key<MdcCoroutineContext>

    /**
     * 恢复 MDC 上下文
     * @param context 上下文
     * @param oldState 旧的 MDC 上下文
     */
    override fun restoreThreadContext(context: CoroutineContext, oldState: Map<String, String>?) {
        oldState?.let {
            // 还原 MDC 上下文
            MDC.setContextMap(oldState)
        } ?: run {
            // 清除 MDC 上下文
            MDC.clear()
        }
    }

    /**
     * 更新 MDC 上下文
     * @param context 上下文
     * @return MDC 上下文
     */
    override fun updateThreadContext(context: CoroutineContext): Map<String, String>? {
        // 获取当前线程的 MDC 上下文副本
        val previous = MDC.getCopyOfContextMap()
        contextMap?.let {
            // 如果 contextMap 不为空，则将 contextMap 设置为当前线程的 MDC 上下文
            MDC.setContextMap(it)
        }
        // 返回之前的 MDC 上下文，以便在协程执行完毕后可以恢复到之前的上下文状态
        return previous
    }
}