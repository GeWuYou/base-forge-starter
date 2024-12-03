package com.gewuyou.security.service;


import com.gewuyou.security.config.entity.IJwtProperties;
import com.gewuyou.security.entity.JwtProperties;
import com.gewuyou.security.enums.TokenType;
import io.jsonwebtoken.Claims;

/**
 * jwt服务接口
 *
 * @author gewuyou
 * @since 2024-11-30 12:07:45
 */
public interface IJwtService {
    /**
     * 生成访问token
     *
     * @param jwtProperties jwt配置
     * @return java.lang.String token
     * @apiNote
     * @since 2023/7/2 19:45
     */
    String generateAccessToken(JwtProperties jwtProperties);

    /**
     * 生成刷新token
     *
     * @param jwtProperties jwt配置
     * @return java.lang.String 刷新令牌
     * @since 2023/7/2 19:45
     */
    String generateRefreshToken(JwtProperties jwtProperties);

    /**
     * 判断token是否有效
     *
     * @param token     令牌
     * @param tokenType token类型
     * @return boolean 有效返回true反之false
     */
    boolean validateToken(String token, TokenType tokenType);

    /**
     * 从令牌中获取数据声明
     *
     * @param token     令牌
     * @param tokenType token类型
     * @return io.jsonwebtoken.Claims
     * @apiNote
     * @since 2023/7/2 20:02
     */
    Claims getClaimsFromToken(String token, TokenType tokenType);
    /**
     * 获取jwt配置
     *
     * @param tokenType token类型
     * @return com.gewuyou.util.config.entity.IJwtProperties
     */
    IJwtProperties getJwtProperties(TokenType tokenType);
}
