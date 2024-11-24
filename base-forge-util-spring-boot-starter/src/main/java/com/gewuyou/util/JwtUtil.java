package com.gewuyou.util;

import com.gewuyou.util.config.entity.IJwtProperties;
import com.gewuyou.util.config.entity.JwtAccessProperties;
import com.gewuyou.util.config.entity.JwtRefreshProperties;
import com.gewuyou.util.entity.JwtProperties;
import com.gewuyou.util.enums.TokenType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.SecretKey;
import java.util.*;

/**
 * Jwt工具类
 *
 * @author gewuyou
 * @since 2024-11-19 12:35:04
 */
@Slf4j
public class JwtUtil {
    /**
     *  授权
     */
    public static final String AUTHORIZATION="Authorization";
    /**
     * 用户信息-用户名
     */
    private static final String USERNAME = "username";
    private static final String AUTHORITIES = "authorities";
    private final JwtAccessProperties jwtAccessProperties;
    private final JwtRefreshProperties jwtRefreshProperties;

    public JwtUtil(JwtAccessProperties jwtAccessProperties, JwtRefreshProperties jwtRefreshProperties) {
        this.jwtAccessProperties = jwtAccessProperties;
        this.jwtRefreshProperties = jwtRefreshProperties;
    }

    /**
     * 生成访问token
     *
     * @param jwtProperties jwt配置
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    public String generateAccessToken(JwtProperties jwtProperties) {
        Map<String, Object> claims = new HashMap<>();
        claims.put(USERNAME, jwtProperties.getUserDetails().getUsername());
        claims.put(AUTHORITIES, jwtProperties.getUserDetails().getAuthorities());
        Map<String, Object> otherClaims = jwtProperties.getOtherClaims();
        if (Objects.nonNull(otherClaims)) {
            claims.putAll(otherClaims);
        }
        // 创建token
        return createAccessToken(claims, jwtProperties);
    }

    /**
     * 从请求中获取AccessToken
     * @param request 请求
     * @return java.lang.String AccessToken
     */
    public String getAccessToken(HttpServletRequest request) {
        if(Objects.isNull(request)){
            throw new NullPointerException("request is null");
        }
        String authHeader = request.getHeader(AUTHORIZATION);
        if(Objects.isNull(authHeader)){
            throw new NullPointerException("authHeader is null");
        }
        if(authHeader.startsWith("Bearer ")){
            return authHeader.substring(7);
        }
        return null;
    }
    /**
     * 生成刷新token
     *
     * @param jwtProperties jwt配置
     * @return java.lang.String 刷新令牌
     * @since 2023/7/2 19:45
     */
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
     * 从数据声明中生成访问令牌
     *
     * @param claims        数据键值对
     * @param jwtProperties jwt配置
     * @return java.lang.String 访问令牌
     * @apiNote
     * @since 2023/7/2 19:50
     */
    public String createAccessToken(Map<String, Object> claims, JwtProperties jwtProperties) {
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
                .issuer(jwtAccessProperties.getIssuer())
                // 设置token有效期
                .expiration(new Date(System.currentTimeMillis() + jwtAccessProperties.getExpiration() * 1000))
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
                .expiration(new Date(System.currentTimeMillis() + jwtRefreshProperties.getExpiration() * 1000))
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
    private SecretKey getKey(String secretKey) {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
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
     * 获取jwt配置
     *
     * @param tokenType token类型
     * @return com.gewuyou.util.config.entity.IJwtProperties
     */
    private IJwtProperties getJwtProperties(TokenType tokenType) {
        return switch (tokenType) {
            case Access -> jwtAccessProperties;
            case Refresh -> jwtRefreshProperties;
        };
    }
    /**
     * 从 JWT 中获取用户 ID
     * @param token 输入的 JWT
     * @return 用户 ID
     */
    public String getUserAuthIdFromToken(String token, TokenType tokenType) {
       return getClaimsFromToken(token, tokenType).getSubject();
    }
    /**
     * 判断token是否过期
     *
     * @param token 令牌
     * @return boolean 未失效
     * @apiNote 过期返回true反之false
     * @since 2023/7/2 20:33
     */
    private boolean isTokenExpired(String token, TokenType tokenType) {
        return getClaimsFromToken(token, tokenType).getExpiration().before(new Date());
    }

    /**
     * 判断token是否有效
     * @param token 令牌
     * @param tokenType token类型
     * @param jti 唯一标识
     * @return boolean 有效返回true反之false
     */
    public boolean isTokenValid(String token, TokenType tokenType,String jti) {
        return getClaimsFromToken(token, tokenType).getId().equals(jti) &&!isTokenExpired(token, tokenType);
    }
    /**
     * 从AccessToken中获取用户名
     *
     * @param accessToken 访问令牌
     * @return 用户名
     */
    public String getUsernameByAccessToken(String accessToken) {
        return getValue(getClaimsFromToken(accessToken, TokenType.Access), USERNAME);
    }

    /**
     * 从AccessToken中获取用户权限
     * @param accessToken 访问令牌
     * @return 用户权限字符串
     */
    public String getAuthoritiesByAccessToken(String accessToken) {
        return getValue(getClaimsFromToken(accessToken, TokenType.Access), AUTHORITIES);
    }

    /**
     * 从claims中获取值
     *
     * @param claims 数据声明
     * @param key    键
     * @return java.lang.String 值
     * @apiNote
     * @since 2023/7/17 21:43
     */
    private String getValue(Claims claims, String key) {
        if(claims.containsKey(key)){
            return claims.get(key).toString();
        }else{
            return null;
        }
    }
}
