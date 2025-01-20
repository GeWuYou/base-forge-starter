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
     * 根据用户唯一标识查询用户详细信息
     * @param principal 用户唯一标识 通常为用户名 手机号 邮箱等
     * @return UserDetails 用户详细信息
     */
    fun loadUserByPrincipal(principal: Any): UserDetails?
}