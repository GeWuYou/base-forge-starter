package com.gewuyou.util;

import com.gewuyou.core.constants.SecurityConstant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

import javax.crypto.SecretKey;
import java.util.Base64;
import java.util.Objects;
import java.util.Optional;

/**
 * Jwt工具类
 *
 * @author gewuyou
 * @since 2024-11-19 12:35:04
 */
@Slf4j
public class JwtUtil {
    /**
     * 从请求中获取AccessToken
     *
     * @param request 请求
     * @return java.lang.String AccessToken
     */
    public static String getAccessToken(HttpServletRequest request) {
        Optional<String> authHeaderOptional = getRequestHeader(request, SecurityConstant.AUTHORIZATION);
        return authHeaderOptional
                .filter(
                        header ->
                                header.startsWith(SecurityConstant.BEARER_PREFIX)
                ).map(
                        header ->
                                header.substring(SecurityConstant.BEARER_PREFIX.length())
                                        .trim()
                ).orElseGet(() -> {
                    log.error("token 格式错误或不存在");
                    return null;
                });
    }


    /**
     * 从请求中获取用户信息
     *
     * @param request 请求
     * @return UserDetails 用户信息
     */
    public static String getUserDetails(HttpServletRequest request) {
        Optional<String> userDetailsOptional = getRequestHeader(request, SecurityConstant.USER_DETAILS);
        return userDetailsOptional.orElseGet(() -> {
            log.error("用户信息不存在");
            return null;
        });
    }

    /**
     * 获取请求头中的值
     *
     * @param request    请求
     * @param headerName 请求头名称
     * @return 请求头值
     */
    private static Optional<String> getRequestHeader(HttpServletRequest request, String headerName) {
        if (Objects.isNull(request)) {
            log.error("请求对象为空");
            return Optional.empty();
        }
        String header = request.getHeader(headerName);
        return Optional.ofNullable(header)
                .filter(h -> !h.isEmpty())
                .map(Optional::of)
                .orElseGet(() -> {
                    log.warn("请求头中没有对应的请求头: {}", headerName);
                    return Optional.empty();
                });
    }

    /**
     * 根据定义的密钥生成加密的key
     *
     * @param secretKey 密钥
     * @return java.security.Key
     * @apiNote
     * @since 2023/7/2 19:53
     */
    public static SecretKey getKey(String secretKey) {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secretKey));
    }

    /**
     * 从令牌中获取数据声明
     *
     * @param token     令牌
     * @param secretKey 密钥
     * @return io.jsonwebtoken.Claims
     * @apiNote
     * @since 2023/7/2 20:02
     */
    public static Claims getClaimsFromToken(String token, String secretKey) {
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
     * 从 JWT 中获取用户 ID
     *
     * @param token 输入的 JWT
     * @return 用户 ID
     */
    public static String getUserAuthIdFromToken(String token, String secretKey) {
        return Objects.requireNonNull(getClaimsFromToken(token, secretKey)).getSubject();
    }

    /**
     * 从AccessToken中获取用户名
     *
     * @param accessToken 访问令牌
     * @return 用户名
     */
    public static String getUsernameByAccessToken(String accessToken, String secretKey) {
        return getValue(Objects.requireNonNull(getClaimsFromToken(accessToken, secretKey)), SecurityConstant.USERNAME);
    }

    /**
     * 从AccessToken中获取用户权限
     *
     * @param accessToken 访问令牌
     * @return 用户权限字符串
     */
    public static String getAuthoritiesByAccessToken(String accessToken, String secretKey) {
        return getValue(Objects.requireNonNull(getClaimsFromToken(accessToken, secretKey)), SecurityConstant.AUTHORITIES);
    }

    /**
     * 从AccessToken中获取用户信息
     *
     * @param accessToken 访问令牌
     * @return UserDetails
     */
    public static UserDetails getUserDetailsByAccessToken(String accessToken, String secretKey) {
        return (UserDetails) getObject(Objects.requireNonNull(getClaimsFromToken(accessToken, secretKey)), SecurityConstant.AUTHORITIES);
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
    public static String getValue(Claims claims, String key) {
        return claims.get(key).toString();
    }

    /**
     * 从claims中获取对象
     *
     * @param claims 数据声明
     * @param key    键
     * @return 对象
     * @since 2023/7/17 21:43
     */
    public static Object getObject(Claims claims, String key) {
        return claims.getOrDefault(key, null);
    }

}
