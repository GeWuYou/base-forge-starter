package com.gewuyou.baseforge.security.authorization.autoconfigure.service.impl

import com.gewuyou.baseforge.jwt.entities.constants.JwtConstant
import com.gewuyou.baseforge.jwt.entities.entity.JwtPayloadData
import com.gewuyou.baseforge.redis.service.CacheService
import com.gewuyou.baseforge.security.authorization.autoconfigure.config.entity.JwtProperties
import com.gewuyou.baseforge.security.authorization.autoconfigure.service.JwtAuthorizationService
import com.gewuyou.baseforge.security.authorization.autoconfigure.validator.JwtValidator
import com.gewuyou.baseforge.security.authorization.entities.exception.AuthorizationException
import com.gewuyou.baseforge.security.authorization.entities.i18n.enums.SecurityAuthorizationResponseInformation

/**
 *JWT 授权服务实现
 *
 * @since 2025-02-01 14:52:17
 * @author gewuyou
 */
class JwtAuthorizationServiceImpl(
    private val jwtValidator: JwtValidator,
    private val cacheService: CacheService,
    private val jwtProperties: JwtProperties
) : JwtAuthorizationService {
    /**
     * 验证token`
     * @param token 访问token
     * @return JwtPayloadData 有效载荷数据 如果token无效则返回null
     */
    override fun validateToken(token: String): JwtPayloadData? {
        return jwtValidator.validateTokenAndGetJwtPayloadData(token)?.let { payloadData ->
            // 验证token是否在黑名单中
            if (jwtValidator
                    .checkAccessTokenHasBlacklist(payloadData.principal, payloadData.deviceId, token)
            ) {
                throw AuthorizationException(SecurityAuthorizationResponseInformation.LoginHasExpired)
            }
            return payloadData
        }
    }

    /**
     * 刷新token续期
     * @param principal 用户唯一标识
     * @param deviceId  设备唯一标识
     * @param refreshToken 刷新token
     */
    override fun refreshTokenRenewal(principal: String, deviceId: String, refreshToken: String) {
        // 生成key
        val key = JwtConstant.REFRESH_TOKEN_CACHE_PREFIX + principal + ":" + deviceId
        // 缓存刷新token
        cacheService[key, refreshToken] = jwtProperties.refreshTokenExpiration
    }

}