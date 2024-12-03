package com.gewuyou.security.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 用户登录Principal
 *
 * @author gewuyou
 * @since 2024-11-05 11:13:43
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Principal {
    /**
     * 登录ID
     */
    private String signInId;
    /**
     * 记住我
     */
    private boolean isRememberMe;
}
