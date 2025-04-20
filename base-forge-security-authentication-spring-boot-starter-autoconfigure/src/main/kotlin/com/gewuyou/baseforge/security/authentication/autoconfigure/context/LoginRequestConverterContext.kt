package com.gewuyou.baseforge.security.authentication.autoconfigure.context

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authentication.autoconfigure.strategy.LoginRequestConverterStrategy
import com.gewuyou.baseforge.security.authentication.entities.entity.request.LoginRequest
import org.springframework.context.ApplicationContext
import org.springframework.security.core.Authentication

/**
 *登录请求转换器上下文
 *
 * @since 2025-02-15 03:28:06
 * @author gewuyou
 */
class LoginRequestConverterContext(
    private val applicationContext: ApplicationContext
) {
    private val converterStrategies = mutableMapOf<String, LoginRequestConverterStrategy>()

    init {
        addConverterStrategies()
    }

    /**
     * 添加登录请求转换器策略
     * @param loginType 登录类型
     * @param converter 转换器策略
     */
    private fun addConverter(loginType: String, converter: LoginRequestConverterStrategy) {
        converterStrategies[loginType] = converter
    }

    /**
     * 执行登录请求转换器策略
     * @param loginRequest 登录请求
     * @return 认证对象
     */
    fun executeStrategy(loginRequest: LoginRequest): Authentication {
        return converterStrategies[loginRequest.getLoginType()]?.convert(loginRequest)
            ?: throw IllegalArgumentException("No converter strategy found for login type ${loginRequest.getLoginType()}")
    }

    /**
     * 添加登录请求转换器策略
     */
    private fun addConverterStrategies() {
        val strategies = applicationContext.getBeansOfType(LoginRequestConverterStrategy::class.java).toMap()
        for ((_, strategy) in strategies) {
            val loginType = strategy.getSupportedLoginType()
            log.info("为登录类型: $loginType 添加登录请求转换器策略 ${strategy::class.java.name}")
            addConverter(loginType, strategy)
        }
    }
}