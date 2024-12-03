package com.gewuyou.security.service.impl;

import com.gewuyou.security.config.entity.IJwtProperties;
import com.gewuyou.security.config.entity.JwtAccessProperties;
import com.gewuyou.security.config.entity.JwtRefreshProperties;
import com.gewuyou.security.entity.JwtProperties;
import com.gewuyou.security.enums.TokenType;
import com.gewuyou.security.manager.JtiManager;
import com.gewuyou.security.service.IJwtService;
import com.gewuyou.util.JwtUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

/**
 * 抽象 JWT 服务实现
 *
 * @author gewuyou
 * @since 2024-11-30 13:33:19
 */
public abstract class AbstractJwtServiceImpl implements IJwtService {
    protected final JtiManager jtiManager;

    protected AbstractJwtServiceImpl(JtiManager jtiManager) {
        this.jtiManager = jtiManager;
    }

    /**
     * 从数据声明中生成访问令牌
     *
     * @param claims        数据键值对
     * @param jwtProperties jwt配置
     * @return java.lang.String 访问令牌
     * @apiNote
     * @since 2023/7/2 19:50
     */
    public String createAccessToken(Map<String, Object> claims, JwtProperties jwtProperties) {
        String jti = jwtProperties.getJti();
        String userAuthId = jwtProperties.getUserAuthId();
        JwtAccessProperties jwtAccessProperties = getJwtAccessProperties();
        return Jwts.builder()
                .claims()
                .add(claims)
                // 设置主题为用户认证ID
                .subject(userAuthId)
                // 设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                // 可以在未登陆前作为身份标识使用
                .id(jti)
                // 颁发时间
                .issuedAt(new Date())
                // 设置颁发者
                .issuer(jwtAccessProperties.getIssuer())
                // 设置token有效期
                .expiration(new Date(System.currentTimeMillis() + jwtAccessProperties.getExpiration()))
                .and()
                .signWith(getKey(jwtAccessProperties.getSecretKey()))
                .compact();
    }

    /**
     * 从数据声明中生成访问令牌
     *
     * @param claims        数据键值对
     * @param jwtProperties jwt配置
     * @return java.lang.String 访问令牌
     * @apiNote
     * @since 2023/7/2 19:50
     */
    public String createRefreshToken(Map<String, Object> claims, JwtProperties jwtProperties) {
        JwtRefreshProperties jwtRefreshProperties = getJwtRefreshProperties();
        return Jwts.builder()
                .claims()
                .add(claims)
                // 设置主题为用户认证ID
                .subject(jwtProperties.getUserAuthId())
                // 设置jti(JWT ID)：是JWT的唯一标识，根据业务需要，这个可以设置为一个不重复的值，主要用来作为一次性token,从而回避重放攻击。
                // 可以在未登陆前作为身份标识使用
                .id(jwtProperties.getJti())
                // 颁发时间
                .issuedAt(new Date())
                // 设置颁发者
                .issuer(jwtRefreshProperties.getIssuer())
                // 设置token有效期
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshProperties.getExpiration()))
                .and()
                .signWith(getKey(jwtRefreshProperties.getSecretKey()))
                .compact();
    }
    /**
     * 根据定义的密钥生成加密的key
     *
     * @param secretKey 密钥
     * @return java.security.Key
     * @apiNote
     * @since 2023/7/2 19:53
     */
    protected SecretKey getKey(String secretKey) {
        return JwtUtil.getKey(secretKey);
    }
    /**
     * 获取jwt配置
     *
     * @param tokenType token类型
     * @return com.gewuyou.util.config.entity.IJwtProperties
     */
    @Override
    public IJwtProperties getJwtProperties(TokenType tokenType) {
        return switch (tokenType) {
            case Access -> getJwtAccessProperties();
            case Refresh -> getJwtRefreshProperties();
        };
    }

    /**
     * 获取jwt access配置
     * @return com.gewuyou.security.config.entity.JwtAccessProperties
     */
    protected abstract JwtAccessProperties getJwtAccessProperties();

    /**
     * 获取jwt refresh配置
     * @return com.gewuyou.security.config.entity.JwtRefreshProperties
     */
    protected abstract JwtRefreshProperties getJwtRefreshProperties();

    /**
     * 从claims中获取值
     *
     * @param claims 数据声明
     * @param key    键
     * @return java.lang.String 值
     * @apiNote
     * @since 2023/7/17 21:43
     */
    protected String getValue(Claims claims, String key) {
        return JwtUtil.getValue(claims, key);
    }
}
