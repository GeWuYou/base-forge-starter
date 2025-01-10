package com.gewuyou.baseforge.security.authorization.autoconfigure.service

import org.springframework.security.core.userdetails.UserDetails

/**
 *授权用户详细信息服务
 *
 * @since 2025-01-07 15:11:14
 * @author gewuyou
 */
fun interface AuthorizationUserDetailsService {
    /**
     * 根据用户名加载用户详细信息
     * @param principal 用户名
     * @return 用户详细信息
     */
    fun loadUserByPrincipal(principal: String): UserDetails
}