package com.gewuyou.baseforge.autoconfigure.web.mapping

import com.gewuyou.baseforge.entities.web.annotation.ApiVersion
import org.springframework.core.annotation.AnnotatedElementUtils
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.servlet.mvc.method.RequestMappingInfo
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping
import java.lang.reflect.Method

/**
 *API 版本请求映射处理程序映射
 *
 * @since 2025-02-04 20:30:44
 * @author gewuyou
 */
class ApiVersionRequestMappingHandlerMapping : RequestMappingHandlerMapping() {
    override fun isHandler(beanType: Class<*>): Boolean {
        // 仅处理标注了 @RestController 注解的类
        return AnnotatedElementUtils.hasAnnotation(beanType, RestController::class.java)
    }

    override fun getMappingForMethod(method: Method, handlerType: Class<*>): RequestMappingInfo? {
        // 获取原始的 @RequestMapping 信息
        val requestMappingInfo = super.getMappingForMethod(method, handlerType) ?: return null
        // 优先获取方法上的注解
        val methodApiVersion = AnnotatedElementUtils.findMergedAnnotation(method, ApiVersion::class.java)
        // 当方法注解存在时构建并返回 RequestMappingInfo
        methodApiVersion?.let {
            return combineVersionMappings(requestMappingInfo, it.value)
        }
        // 获取类上的 @ApiVersion 注解
        val classApiVersion = AnnotatedElementUtils.findMergedAnnotation(handlerType, ApiVersion::class.java)
        classApiVersion?.let {
            return combineVersionMappings(requestMappingInfo, it.value)
        }
        // 当类注解不存在时返回原始的 RequestMappingInfo
        return requestMappingInfo
    }

    /**
     * 组合版本路径，支持多个版本
     * @param originalMapping 原始的 RequestMappingInfo
     * @param versions 版本数组
     * @return 组合后的 RequestMappingInfo
     */
    private fun combineVersionMappings(
        originalMapping: RequestMappingInfo,
        versions: Array<out String>
    ): RequestMappingInfo {
        return versions
            .map {
                // 加上版本前缀
                RequestMappingInfo
                    .paths("/api/$it")
                    .build()
            }.reduce {
                // 组合
                    acc, mapping ->
                acc.combine(mapping)
            }
            // 组合原始 RequestMapping
            .combine(originalMapping)
    }
}