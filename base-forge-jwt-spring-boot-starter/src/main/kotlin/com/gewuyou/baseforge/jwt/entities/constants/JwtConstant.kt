package com.gewuyou.baseforge.jwt.entities.constants

/**
 * JWT 常量
 *
 * @author gewuyou
 * @since 2024-12-31 16:11:21
 */
object JwtConstant {
    /**
     * 设备唯一标识
     */
    const val DEVICE_ID: String = "deviceId"

    /**
     * 刷新token缓存前缀
     */
    const val REFRESH_TOKEN_CACHE_PREFIX: String = "refresh_token:"

    /**
     * 黑名单token缓存前缀
     */
    const val BLACKLIST_ACCESS_TOKEN_CACHE_PREFIX: String = "blacklist_access_token:"
}
