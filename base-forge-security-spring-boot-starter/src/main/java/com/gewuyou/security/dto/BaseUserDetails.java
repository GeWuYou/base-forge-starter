package com.gewuyou.security.dto;

import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * 基本用户详细信息
 *
 * @author gewuyou
 * @since 2024-11-04 00:24:35
 */
@Data
public class BaseUserDetails implements UserDetails {
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 用户认证ID
     */
    private String userAuthId;
    /**
     * 权限列表
     */
    private List<GrantedAuthority> authorities;
    /**
     * 账户是否未过期
     */
    private boolean accountNonExpired;
    /**
     * 账户是否未锁定
     */
    private boolean accountNonLocked;
    /**
     * 凭证是否未过期
     */
    private boolean credentialsNonExpired;
    /**
     * 账户是否可用
     */
    private boolean enabled;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
