package com.gewuyou.baseforge.security.authentication.entities.entity

import org.springframework.security.core.GrantedAuthority

/**
 *用户详细信息
 * 之所以要定义这个接口是因为SpringSecurity提供的UserDetails接口提供的只针对用户名密码，扩展性不好
 * @since 2025-02-15 16:21:30
 * @author gewuyou
 */
interface UserDetails {

    /**
     * Returns the authorities granted to the user. Cannot return <code>null</code>.
     * @return the authorities, sorted by natural key (never <code>null</code>)
     */
   fun getAuthorities(): Collection<GrantedAuthority>

    /**
     * The identity of the principal being authenticated. In the case of an authentication
     * request with username and password, this would be the username. Callers are
     * expected to populate the principal for an authentication request.
     * <p>
     * @return the <code>Principal</code> being authenticated or the authenticated
     * principal after authentication.
     */
   fun getPrincipal(): Any

    /**
     * The credentials that prove the principal is correct. This is usually a password,
     * but could be anything relevant to the <code>AuthenticationManager</code>. Callers
     * are expected to populate the credentials.
     * @return the credentials that prove the identity of the <code>Principal</code>
     */
   fun getCredentials(): Any

    /**
     * 返回用户的唯一标识，与Principal不同的是UserOnlyIdentity一般来说是固定的比如用户的ID，而Principal是用户的身份标识，比如用户名，邮箱等
     * @return 用户的唯一标识
     */
   fun getUserOnlyIdentity(): Any
    /**
     * Indicates whether the user's account has expired. An expired account cannot be
     * authenticated.
     * @return `true` if the user's account is valid (ie non-expired),
     * `false` if no longer valid (ie expired)
     */
    fun isAccountNonExpired(): Boolean {
        return true
    }

    /**
     * Indicates whether the user is locked or unlocked. A locked user cannot be
     * authenticated.
     * @return `true` if the user is not locked, `false` otherwise
     */
    fun isAccountNonLocked(): Boolean {
        return true
    }

    /**
     * Indicates whether the user's credentials (password) has expired. Expired
     * credentials prevent authentication.
     * @return `true` if the user's credentials are valid (ie non-expired),
     * `false` if no longer valid (ie expired)
     */
    fun isCredentialsNonExpired(): Boolean {
        return true
    }

    /**
     * Indicates whether the user is enabled or disabled. A disabled user cannot be
     * authenticated.
     * @return `true` if the user is enabled, `false` otherwise
     */
    fun isEnabled(): Boolean {
        return true
    }
}