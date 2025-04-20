package com.gewuyou.baseforge.autoconfigure.async.launcher

import com.gewuyou.baseforge.autoconfigure.async.context.MdcCoroutineContext
import com.gewuyou.baseforge.core.extension.log
import kotlinx.coroutines.*
import org.slf4j.MDC

/**
 *协程启动器
 *
 * @since 2025-01-23 11:14:28
 * @author gewuyou
 */
object CoroutineLauncher {
    /**
     * 启动一个协程任务，返回 [Deferred]，支持 MDC 传递和超时控制。
     *
     * @param block 协程任务代码块
     * @param dispatcher 协程调度器，默认为 [Dispatchers.Default]
     * @param timeout 超时时间，单位毫秒，默认为 5000ms
     * @return [Deferred]，可通过 `await()` 获取结果
     */
    fun <T> launch(
        block: suspend CoroutineScope.() -> T,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        timeout: Long = 5000L
    ): Deferred<T> {
        val mdcContext = MDC.getCopyOfContextMap().toMap()
        return CoroutineScope(dispatcher).async {
            withContext(MdcCoroutineContext(mdcContext)) {
                try {
                    withTimeout(timeout) {
                        block()
                    }
                } catch (e: TimeoutCancellationException) {
                    log.error("Coroutine execution timed out after ${timeout}ms", e)
                    throw e
                } catch (e: Exception) {
                    log.error("Exception in coroutine execution", e)
                    throw e
                }
            }
        }
    }

    /**
     * 启动一个协程任务，支持回调机制，适合完全异步场景。
     *
     * @param block 协程任务代码块
     * @param dispatcher 协程调度器，默认为 [Dispatchers.Default]
     * @param timeout 超时时间，单位毫秒，默认为 5000ms
     * @param onSuccess 成功回调
     * @param onError 异常回调
     */
    fun <T> launchAsync(
        block: suspend CoroutineScope.() -> T,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        timeout: Long = 5000L,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        val mdcContext = MDC.getCopyOfContextMap().toMap()
        CoroutineScope(dispatcher).launch {
            withContext(MdcCoroutineContext(mdcContext)) {
                try {
                    val result = withTimeout(timeout) { block() }
                    // 异步成功回调
                    onSuccess(result)
                } catch (e: TimeoutCancellationException) {
                    log.error("Coroutine execution timed out after ${timeout}ms", e)
                    onError(e)
                } catch (e: Exception) {
                    log.error("Exception in coroutine execution", e)
                    onError(e)
                }
            }
        }
    }

    /**
     * 启动一个协程任务，返回 [Deferred]，支持 MDC 传递和超时控制。
     *
     * @param block 协程任务代码块
     * @param dispatcher 协程调度器，默认为 [Dispatchers.Default]
     * @return [Deferred]，可通过 `await()` 获取结果
     */
    fun <T> launch(
        block: suspend CoroutineScope.() -> T,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
    ): Deferred<T> {
        val mdcContext = MDC.getCopyOfContextMap().toMap()
        return CoroutineScope(dispatcher).async {
            withContext(MdcCoroutineContext(mdcContext)) {
                try {
                    block()
                } catch (e: Exception) {
                    log.error("Exception in coroutine execution", e)
                    throw e
                }
            }
        }
    }

    /**
     * 启动一个协程任务，支持回调机制，适合完全异步场景。
     *
     * @param block 协程任务代码块
     * @param dispatcher 协程调度器，默认为 [Dispatchers.Default]
     * @param onSuccess 成功回调
     * @param onError 异常回调
     */
    fun <T> launchAsync(
        block: suspend CoroutineScope.() -> T,
        dispatcher: CoroutineDispatcher = Dispatchers.Default,
        onSuccess: (T) -> Unit = {},
        onError: (Throwable) -> Unit = {}
    ) {
        val mdcContext = MDC.getCopyOfContextMap().toMap()
        CoroutineScope(dispatcher).launch {
            withContext(MdcCoroutineContext(mdcContext)) {
                try {
                    // 异步成功回调
                    onSuccess(block())
                } catch (e: Exception) {
                    log.error("Exception in coroutine execution", e)
                    onError(e)
                }
            }
        }
    }

}