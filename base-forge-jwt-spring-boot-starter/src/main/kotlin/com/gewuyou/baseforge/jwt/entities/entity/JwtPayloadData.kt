package com.gewuyou.baseforge.jwt.entities.entity

/**
 *JWT 负载数据
 *
 * @since 2025-01-02 23:15:46
 * @author gewuyou
 */
data class JwtPayloadData(
    /**
     * 自定义声明
     */
    val claims: Map<String, Any>,
    /**
     * JWT ID
     */
    val jti: String,
    /**
     * 用户ID
     */
    val principal: String,
    /**
     * 设备ID
     */
    val deviceId: String,
)