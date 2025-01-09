package com.gewuyou.baseforge.jwt.autoconfigure.handler

import com.gewuyou.baseforge.jwt.entities.entity.JwtPayloadData
import java.util.*

/**
 * JWT 处理程序
 *
 * @author gewuyou
 * @since 2024-12-31 15:47:34
 */
interface JwtHandler {
    /**
     * 生成访问token
     *
     * @param principal   用户身份标识
     * @param deviceId    设备标识
     * @param otherClaims 其他声明
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    fun generateToken(principal: String, deviceId: String ,otherClaims: Map<String, Any>?): String


    /**
     * 生成刷新token
     *
     * @return java.lang.String 刷新令牌
     * @since 2023/7/2 19:45
     */
    fun generateRefreshToken(): String {
        return UUID.randomUUID().toString()
    }

    /**
     * 验证令牌并获取用户信息
     *
     * @param token     令牌
     * @return jwt载荷数据
     */
    fun validateTokenAndGetClaims(token: String): JwtPayloadData?
}
