package com.gewuyou.baseforge.security.authentication.autoconfigure.service.impl

import com.gewuyou.baseforge.security.authentication.autoconfigure.service.UserCacheService
import com.gewuyou.baseforge.security.authentication.entities.entity.UserDetails

/**
 *空用户缓存服务
 *
 * @since 2025-02-16 23:53:44
 * @author gewuyou
 */
class NullUserCacheService: UserCacheService {
    /**
     * 从缓存中获取用户信息
     * @param principal 用户标识
     * @return 用户信息 返回null表示缓存中没有该用户信息或信息已过期
     */
    override fun getUserFromCache(principal: String): UserDetails? {
        return null
    }

    /**
     * 将用户信息缓存到缓存中 注意，请将过期时间设置得尽可能短，以防止缓存与数据库出现数据不一致
     * @param userDetails 用户信息
     */
    override fun putUserToCache(userDetails: UserDetails) {
        // ignore
    }

    /**
     * 从缓存中移除用户信息
     * @param principal 用户标识
     */
    override fun removeUserFromCache(principal: String) {
        // ignore
    }
}