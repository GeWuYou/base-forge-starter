package com.gewuyou.baseforge.feign.config

import feign.Retryer
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 *feign 自动配置
 *
 * @since 2025-03-03 11:12:26
 * @author gewuyou
 */
@Configuration
class FeignAutoConfiguration {
    /**
     * 重试机制配置: 最大请求次数为5，初始间隔时间为100ms，下次间隔时间1.5倍递增，重试间最大间隔时间为1s，
     */
    @Bean
    @ConditionalOnMissingBean(Retryer::class)
    fun createRetryer(): Retryer {
        return Retryer.Default()
    }
}