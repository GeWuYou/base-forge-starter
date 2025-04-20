package com.gewuyou.baseforge.security.authentication.entities.extension

import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity


/**
 *HTTP 安全性配置扩展
 *
 * @since 2025-01-07 17:55:53
 * @author gewuyou
 */


/**
 * 清理不需要的配置 关闭清单如下：
 * 1. csrf
 * 2. formLogin
 * 3. sessionManagement
 * 4. anonymous
 * 5. requestCache
 * 6. httpBasic
 * 7. rememberMe
 */
fun HttpSecurity.cleanUnNeedConfig(): HttpSecurity {
    return this
        // 关闭csrf
        .csrf { it.disable() }
         // 关闭form登录
        .formLogin { it.disable() }
         // 关闭session管理
        .sessionManagement{it.disable()}
        // 关闭匿名
        .anonymous{it.disable()}
        // 关闭请求缓存
        .requestCache{it.disable()}
        // 关闭基本认证
        .httpBasic{it.disable()}
        // 关闭记住我
        .rememberMe{it.disable()}
}

/**
 * 清理不需要的配置 关闭清单如下：
 * 1. csrf
 * 2. formLogin
 * 3. anonymous
 * 4. requestCache
 * 5. httpBasic
 * 6. logout
 */
fun ServerHttpSecurity.cleanUnNeedConfig(): ServerHttpSecurity {
    return this
        // 关闭csrf
        .csrf { it.disable() }
         // 关闭form登录
        .formLogin { it.disable() }
        // 关闭匿名
        .anonymous{it.disable()}
        // 关闭请求缓存
        .requestCache{it.disable()}
        // 关闭基本认证
        .httpBasic{it.disable()}
        // 关闭登出
        .logout { it.disable() }
}
