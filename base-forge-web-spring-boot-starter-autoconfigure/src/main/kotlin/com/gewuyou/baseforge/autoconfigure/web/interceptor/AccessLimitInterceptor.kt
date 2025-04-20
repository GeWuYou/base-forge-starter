package com.gewuyou.baseforge.autoconfigure.web.interceptor

import com.fasterxml.jackson.databind.ObjectMapper
import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.entities.web.annotation.AccessLimit
import com.gewuyou.baseforge.entities.web.entity.Result
import com.gewuyou.baseforge.entities.web.i18n.enums.WebResponseInformation
import com.gewuyou.baseforge.redis.service.CacheService
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.MessageSource
import org.springframework.http.MediaType
import org.springframework.lang.NonNull
import org.springframework.web.method.HandlerMethod
import org.springframework.web.servlet.HandlerInterceptor
import java.io.IOException
import java.io.OutputStream
import java.nio.charset.StandardCharsets

/**
 * 访问限制拦截器
 *
 * @author gewuyou
 * @since 2024-05-19 下午4:39:17
 */
class AccessLimitInterceptor(
    private val cacheService: CacheService,
    private val objectMapper: ObjectMapper,
    private val i18nMessageSource: MessageSource
) : HandlerInterceptor {
    @Throws(Exception::class)
    override fun preHandle(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull handler: Any
    ): Boolean {
        if (handler !is HandlerMethod) {
            return true
        }
        // 获取方法上的 AccessLimit 注解
        val accessLimit = handler.getMethodAnnotation(
            AccessLimit::class.java
        )
        if (accessLimit == null) {
            return true
        }
        // 获取访问时间间隔
        val seconds = accessLimit.seconds
        // 获取访问次数
        val maxCount = accessLimit.maxCount
        // 设置键
        val key = "limit:" + System.currentTimeMillis() / 1000 + ":" + seconds
        val l = cacheService.incrExpire(key, seconds.toLong())
        if (l > maxCount) {
            render(response, Result.failure<Any>(WebResponseInformation.TOO_MANY_REQUESTS, i18nMessageSource))
            log.warn("{}请求次数超过每{}秒{}次限制", key, seconds, maxCount)
            return false
        }
        return true
    }

    private fun render(response: HttpServletResponse, result: Result<*>) {
        try {
            response.contentType = MediaType.APPLICATION_JSON_VALUE
            val out: OutputStream = response.outputStream
            val str = objectMapper.writeValueAsString(result)
            out.write(str.toByteArray(StandardCharsets.UTF_8))
            out.flush()
            out.close()
        } catch (e: IOException) {
            log.error("渲染响应失败", e)
        }
    }
}
