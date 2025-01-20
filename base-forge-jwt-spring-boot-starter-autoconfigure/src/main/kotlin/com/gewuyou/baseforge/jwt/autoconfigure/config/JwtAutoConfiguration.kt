package com.gewuyou.baseforge.jwt.autoconfigure.config

import com.gewuyou.baseforge.jwt.autoconfigure.config.entity.JwtProperties
import com.gewuyou.baseforge.jwt.autoconfigure.handler.DefaultJwtHandler
import com.gewuyou.baseforge.jwt.autoconfigure.handler.JwtHandler
import com.gewuyou.baseforge.jwt.autoconfigure.service.JwtService
import com.gewuyou.baseforge.jwt.autoconfigure.service.impl.DefaultJwtService
import com.gewuyou.baseforge.redis.service.CacheService
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

/**
 * jwt自动配置类
 *
 * @author gewuyou
 * @since 2024-12-31 15:29:40
 */
@Configuration
@EnableConfigurationProperties(JwtProperties::class)
class JwtAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean(JwtHandler::class)
    fun createJwtHandler(jwtProperties: JwtProperties): JwtHandler {
        return DefaultJwtHandler(jwtProperties)
    }

    @Bean
    @ConditionalOnMissingBean(JwtService::class)
    fun createJwtService(cacheService: CacheService, jwtHandler: JwtHandler, jwtProperties: JwtProperties): JwtService {
        return DefaultJwtService(cacheService, jwtHandler, jwtProperties)
    }
}
