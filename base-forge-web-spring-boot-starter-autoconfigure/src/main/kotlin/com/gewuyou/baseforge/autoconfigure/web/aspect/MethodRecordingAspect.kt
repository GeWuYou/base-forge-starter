package com.gewuyou.baseforge.autoconfigure.web.aspect

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.entities.web.annotation.MethodRecording
import org.aspectj.lang.ProceedingJoinPoint
import org.aspectj.lang.annotation.Around
import org.aspectj.lang.annotation.Aspect
import org.springframework.stereotype.Component

/**
 *方法记录切面
 *
 * @since 2025-01-23 14:25:04
 * @author gewuyou
 */
@Aspect
@Component
class MethodRecordingAspect {
    /**
     * 切面逻辑，用于记录方法的执行情况
     */
    @Around("@annotation(methodRecording)")
    fun methodRecording(joinPoint: ProceedingJoinPoint, methodRecording: MethodRecording): Any? {
        val methodName = joinPoint.signature.name
        val className = joinPoint.signature.declaringTypeName
        val description = methodRecording.description
        val args = joinPoint.args
        val logStr = StringBuilder("开始执行方法: ${className}.${methodName}\n${description}\n")
        // 打印方法开始执行的日志，包括入参
        if (methodRecording.printArgs) {
            logStr.append("入参: ${args.joinToString()}\n")
        }
        log.info(logStr.toString())
        val startTime = System.currentTimeMillis()
        return try {
            // 执行目标方法
            val result = joinPoint.proceed()
            val endTime = System.currentTimeMillis()
            val duration = endTime - startTime
            logStr.setLength(0)
            logStr.append("方法 ${className}.${methodName} 执行完成\n")
            if (methodRecording.printResult) {
                logStr.append("方法返回值: $result\n")
            }
            if (methodRecording.printTime) {
                logStr.append("执行耗时: ${duration}ms")
            }
            // 打印方法执行完成的日志
            log.info(logStr.toString())
            result
        } catch (ex: Throwable) {
            log.error("方法 ${className}.${methodName} 执行失败: ${ex.message}", ex)
            throw ex
        }
    }
}