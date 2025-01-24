package com.gewuyou.baseforge.autoconfigure.async.config

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *协程 Dispatcher 自动配置
 *
 * @since 2025-01-24 17:57:01
 * @author gewuyou
 */
@Configuration
class CoroutineDispatcherAutoConfiguration {
    /**
    * 创建默认的协程 Dispatcher
    */
    @Bean(name = ["defaultCoroutineDispatcher"])
    @ConditionalOnMissingBean(name = ["defaultCoroutineDispatcher"])
    fun createCoroutineDispatcher():CoroutineDispatcher {
        return Dispatchers.Default
    }

    /**
    * 创建 IO 协程 Dispatcher
    */
    @Bean(name = ["ioCoroutineDispatcher"])
    @ConditionalOnMissingBean(name = ["ioCoroutineDispatcher"])
    fun createIoCoroutineDispatcher():CoroutineDispatcher {
        return Dispatchers.IO
    }
}