package com.gewuyou.baseforge.security.authorization.autoconfigure.validator

import com.gewuyou.baseforge.jwt.entities.entity.JwtPayloadData

/**
 * JWT 验证器接口
 *
 * @since 2025-02-01 11:13:46
 * @author gewuyou
 */
interface JwtValidator {
    /**
     * 验证令牌并获取用户信息
     *
     * @param token     令牌
     * @return jwt载荷数据
     */
    fun validateTokenAndGetJwtPayloadData(token: String): JwtPayloadData?
    /**
     * 检查访问token是否在黑名单中
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param token     访问token
     */
    fun checkAccessTokenHasBlacklist(principal: String, deviceId: String, token: String): Boolean
}