package com.gewuyou.security.checker;

import org.springframework.security.core.userdetails.UserDetails;

/**
 * 用户详细信息检查器
 *
 * @author gewuyou
 * @since 2024-11-28 00:18:36
 */
@FunctionalInterface
public interface UserDetailsChecker {
    /**
     * 检查用户详细信息
     * @param userDetails 用户详细信息
     */
    void check(UserDetails userDetails);
}
