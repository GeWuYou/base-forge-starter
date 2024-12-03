package com.gewuyou.security.service.impl;


import com.gewuyou.core.constants.SecurityConstant;
import com.gewuyou.security.config.entity.JwtAccessProperties;
import com.gewuyou.security.config.entity.JwtRefreshProperties;
import com.gewuyou.security.entity.JwtProperties;
import com.gewuyou.security.enums.TokenType;
import com.gewuyou.security.manager.JtiManager;
import com.gewuyou.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * jwt服务
 *
 * @author gewuyou
 * @since 2024-11-30 12:08:16
 */
@Slf4j
public class JwtServiceImpl extends AbstractJwtServiceImpl {
    private final JwtAccessProperties jwtAccessProperties;
    private final JwtRefreshProperties jwtRefreshProperties;

    public JwtServiceImpl(
            JwtAccessProperties jwtAccessProperties,
            JwtRefreshProperties jwtRefreshProperties,
            JtiManager jtiManager
    ) {
        super(jtiManager);
        this.jwtAccessProperties = jwtAccessProperties;
        this.jwtRefreshProperties = jwtRefreshProperties;
    }

    /**
     * 获取jwt access配置
     *
     * @return com.gewuyou.security.config.entity.JwtAccessProperties
     */
    @Override
    protected JwtAccessProperties getJwtAccessProperties() {
        return this.jwtAccessProperties;
    }

    /**
     * 获取jwt refresh配置
     *
     * @return com.gewuyou.security.config.entity.JwtRefreshProperties
     */
    @Override
    protected JwtRefreshProperties getJwtRefreshProperties() {
        return this.jwtRefreshProperties;
    }

    /**
     * 生成访问token
     *
     * @param jwtProperties jwt配置
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    @Override
    public String generateAccessToken(JwtProperties jwtProperties) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(SecurityConstant.USERNAME, jwtProperties.getUserDetails().getUsername());
        claims.put(SecurityConstant.AUTHORITIES, jwtProperties.getUserDetails().getAuthorities());
        claims.put(SecurityConstant.USER_DETAILS, jwtProperties.getUserDetails());
        Map<String, Object> otherClaims = jwtProperties.getOtherClaims();
        if (Objects.nonNull(otherClaims)) {
            claims.putAll(otherClaims);
        }
        // 创建token
        return createAccessToken(claims, jwtProperties);
    }

    /**
     * 生成刷新token
     *
     * @param jwtProperties jwt配置
     * @return java.lang.String 刷新令牌
     * @since 2023/7/2 19:45
     */
    @Override
    public String generateRefreshToken(JwtProperties jwtProperties) {
        Map<String, Object> claims = new HashMap<>();
        Map<String, Object> otherClaims = jwtProperties.getOtherClaims();
        if (Objects.nonNull(otherClaims)) {
            claims.putAll(otherClaims);
        }
        // 创建token
        return createRefreshToken(claims, jwtProperties);
    }

    /**
     * 判断token是否有效
     *
     * @param token     令牌
     * @param tokenType token类型
     * @return boolean 有效返回true反之false
     */
    @Override
    public boolean validateToken(String token, TokenType tokenType) {
        // 先判断token是否过期，过期直接返回false
        if (isTokenExpired(token, tokenType)) {
            return false;
        }
        // 尝试解析token，如果返回null说明解析失败，返回false
        Claims claims = getClaimsFromToken(token, tokenType);
        if (Objects.isNull(claims)) {
            return false;
        }
        // 调用jti管理器判断token是否有效
        return jtiManager.isJtiValid(JwtUtil.getUserAuthIdFromToken(token, getJwtProperties(tokenType).getSecretKey()),claims.getId());
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token     令牌
     * @param tokenType token类型
     * @return io.jsonwebtoken.Claims
     * @apiNote
     * @since 2023/7/2 20:02
     */
    @Override
    public Claims getClaimsFromToken(String token, TokenType tokenType) {
        String secretKey = getJwtProperties(tokenType).getSecretKey();
        try {
            return Jwts.parser()
                    .verifyWith(getKey(secretKey))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            log.error("解析token失败", e);
            return null;
        }
    }

    /**
     * 判断token是否过期
     *
     * @param token     令牌
     * @param tokenType token类型
     * @return boolean 未失效
     * @apiNote 过期返回true反之false
     * @since 2023/7/2 20:33
     */
    private boolean isTokenExpired(String token, TokenType tokenType) {
        return getClaimsFromToken(token, tokenType).getExpiration().before(new Date());
    }
}
