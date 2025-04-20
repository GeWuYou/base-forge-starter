package com.gewuyou.baseforge.autoconfigure.async.aspect

import org.aspectj.lang.annotation.After
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.annotation.Pointcut
import org.slf4j.MDC
import org.springframework.stereotype.Component

/**
 *MDC 上下文切面
 *
 * @since 2025-01-22 14:48:14
 * @author gewuyou
 */
@Aspect
@Component
class AsyncMdcContextAspect {
    /**
     * 拦截标有 @AsyncMDC 注解的方法
     */
    @Pointcut("@annotation(com.gewuyou.baseforge.autoconfigure.async.annotation.AsyncMDC)")
    fun asyncMDCMethods() {
        // 定义切入点表达式，拦截标有 @AsyncMDC 注解的方法
    }

    /**
     * Async 方法执行前，设置 MDC 上下文
     */
    @Before("asyncMDCMethods()")
    fun beforeAsyncMethod(){
        // 复制当前线程的 MDC 上下文
        MDC.getCopyOfContextMap()?.let {
            // 设置 MDC 上下文
            MDC.setContextMap(it)
        }
    }

    /**
     * Async 方法执行后，清除 MDC 上下文
     */
    @After("asyncMDCMethods()")
    fun afterAsyncMethod(){
        MDC.clear()
    }
}