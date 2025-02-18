package com.gewuyou.baseforge.security.authentication.autoconfigure.provider

import com.gewuyou.baseforge.core.extension.log
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.UserCacheService
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.UserDetailsService
import com.gewuyou.baseforge.security.authentication.entities.entity.UserDetails
import com.gewuyou.baseforge.security.authentication.entities.token.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper
import org.springframework.security.crypto.password.PasswordEncoder

/**
 * 用户名密码身份验证提供程序<br></br>
 * 用于处理用户名密码身份验证，继承自AbstractAuthenticationProvider，实现了具体的身份验证逻辑
 *
 * @author gewuyou
 * @since 2024-12-30 15:54:35
 */
class UsernamePasswordAuthenticationProvider(
    userCacheService: UserCacheService,
    /**
     * 权限映射器
     */
    private val authoritiesMapper: GrantedAuthoritiesMapper,
    passwordEncoder: PasswordEncoder,
    userDetailsService: UserDetailsService
) :
    AbstractPrincipalPasswordAuthenticationProvider(userCacheService, userDetailsService, passwordEncoder) {
    /**
     * 创建成功的认证
     *
     * @param authentication 认证
     * @param user           用户详情
     * @return 成功的认证
     */
    override fun createSuccessAuthentication(authentication: Authentication, user: UserDetails): Authentication {
        val result = UsernamePasswordAuthenticationToken.authenticated(
            user, authentication.credentials, authoritiesMapper.mapAuthorities(user.getAuthorities())
        )
        log.debug("身份验证成功用户: {}", result.name)
        // 设置用户详情
        result.details = authentication.details
        return result
    }

    /**
     * 是否支持指定的认证类型
     *
     * @param authentication 认证类型
     * @return 是否支持
     */
    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}
