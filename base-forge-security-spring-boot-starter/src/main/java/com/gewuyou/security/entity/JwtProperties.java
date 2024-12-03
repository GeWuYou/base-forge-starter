package com.gewuyou.security.entity;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

/**
 * JWT 属性
 *
 * @author gewuyou
 * @since 2024-11-20 16:43:02
 */
@Getter
@Setter
@Builder
public class JwtProperties {
    /**
     * 用户认证ID
     */
    private String userAuthId;
    /**
     * 用户信息
     */
    private UserDetails userDetails;
    /**
     * 其他自定义信息
     */
    private Map<String, Object> otherClaims;
    /**
     * jti 标识符
     */
    private String jti;
}
