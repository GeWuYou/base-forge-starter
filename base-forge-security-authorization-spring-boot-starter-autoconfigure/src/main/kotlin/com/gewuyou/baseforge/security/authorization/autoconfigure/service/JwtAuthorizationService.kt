package com.gewuyou.baseforge.security.authorization.autoconfigure.service

import com.gewuyou.baseforge.jwt.entities.entity.JwtPayloadData

/**
 *JWT 授权服务
 *
 * @since 2025-01-07 09:57:44
 * @author gewuyou
 */
interface JwtAuthorizationService {
    /**
     * 验证token`
     * @param token 访问token
     * @return JwtPayloadData 有效载荷数据 如果token无效则返回null
     */
    fun validateToken(token: String): JwtPayloadData?

    /**
     * 刷新token续期
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param refreshToken 刷新token
     */
    fun refreshTokenRenewal(principal: String, deviceId: String, refreshToken: String)

}