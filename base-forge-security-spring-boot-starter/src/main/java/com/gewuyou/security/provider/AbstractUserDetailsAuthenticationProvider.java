package com.gewuyou.security.provider;

import com.gewuyou.security.checker.UserDetailsChecker;
import com.gewuyou.security.entity.Principal;
import com.gewuyou.security.exception.SignInIdNotFoundException;
import com.gewuyou.security.token.NormalLoginAuthenticationToken;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.authority.mapping.NullAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Objects;
import java.util.Optional;

/**
 * 抽象用户信息身份验证提供程序
 *
 * @author gewuyou
 * @since 2024-11-05 20:12:41
 */
@Setter
@Slf4j
public abstract class AbstractUserDetailsAuthenticationProvider implements AuthenticationProvider {
    @Getter
    private final UserCache userCache;
    /**
     * 是否强制使用字符串作为用户标识
     */
    @Getter
    private boolean forcePrincipalAsString = false;
    /**
     * 前置身份验证检查
     */
    @Getter
    private UserDetailsChecker preAuthenticationChecks;
    /**
     * 后置身份验证检查
     */
    @Getter
    private UserDetailsChecker postAuthenticationChecks ;
    /**
     * 权限映射器
     */
    private GrantedAuthoritiesMapper authoritiesMapper = new NullAuthoritiesMapper();

    public AbstractUserDetailsAuthenticationProvider(
            UserDetailsChecker preAuthenticationChecks,
            UserDetailsChecker postAuthenticationChecks,
            UserCache userCache
    ) {
        this.preAuthenticationChecks = preAuthenticationChecks;
        this.postAuthenticationChecks = postAuthenticationChecks;
        this.userCache = userCache;
    }

    /**
     * 额外的身份验证检查
     *
     * @param userDetails    用户详情
     * @param authentication 登录认证令牌
     * @throws AuthenticationException 认证异常
     */
    protected abstract void additionalAuthenticationChecks(UserDetails userDetails, NormalLoginAuthenticationToken authentication) throws AuthenticationException;

    /**
     * 检索用户信息
     *
     * @param signInId       用户登录ID(用户名 or 手机号 or 邮箱)
     * @param authentication 登录token
     * @return 用户信息
     */
    protected abstract UserDetails retrieveUser(String signInId, NormalLoginAuthenticationToken authentication) throws AuthenticationException;

    /**
     * 认证
     *
     * @param authentication 登录token
     * @return 登录token
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        if (authentication instanceof NormalLoginAuthenticationToken normalLoginAuthenticationToken) {
            // 获取用户输入的信息
            Principal principal = (Principal) Optional
                    .of(normalLoginAuthenticationToken.getPrincipal())
                    .orElseThrow(() -> new IllegalArgumentException("User Principal be null"));
            boolean cacheWasUsed = true;
            // 尝试从缓存中获取用户信息
            UserDetails user = this.userCache.getUserFromCache(principal.getSignInId());
            if (Objects.isNull(user)) {
                cacheWasUsed = false;
                try {
                    user = this.retrieveUser(principal.getSignInId(), normalLoginAuthenticationToken);
                } catch (SignInIdNotFoundException e) {
                    log.debug("Failed to find user with signInId: {}", principal.getSignInId());
                    throw e;
                }
            }
            try {
                // 前置身份验证检查
                this.preAuthenticationChecks.check(user);
                additionalAuthenticationChecks(user, normalLoginAuthenticationToken);
            } catch (AuthenticationException e) {
                if (!cacheWasUsed) {
                    throw e;
                }
                // 缓存中没有用户信息，重新获取用户信息
                cacheWasUsed = false;
                user = this.retrieveUser(principal.getSignInId(), normalLoginAuthenticationToken);
                // 前置身份验证检查
                this.preAuthenticationChecks.check(user);
                // 添加额外的身份验证检查
                additionalAuthenticationChecks(user, normalLoginAuthenticationToken);
            }
            Object principalToReturn = getPrincipalToReturn(user, cacheWasUsed);
            return createSuccessAuthentication(principalToReturn, normalLoginAuthenticationToken, user);
        } else {
            throw new IllegalArgumentException("Authentication is not NormalLoginAuthenticationToken");
        }
    }

    /**
     * 获取需要返回的标识
     * @param user 用户详情
     * @param cacheWasUsed 是否使用缓存
     * @return 需要返回的标识
     */
    private Object getPrincipalToReturn(UserDetails user, boolean cacheWasUsed) {
        // 后置身份验证检查
        this.postAuthenticationChecks.check(user);
        // 缓存用户信息
        if (!cacheWasUsed) {
            this.userCache.putUserInCache(user);
        }
        Object principalToReturn = user;
        if (this.forcePrincipalAsString) {
            principalToReturn = user.getUsername();
        }
        return principalToReturn;
    }

    /**
     * 创建成功的认证
     *
     * @param principal      用户标识
     * @param user           用户详情
     * @param authentication 认证
     * @return 成功的认证
     */
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        NormalLoginAuthenticationToken result = NormalLoginAuthenticationToken.authenticated(
                principal,
                authentication.getCredentials(),
                this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        log.debug("Authentication success for {}", result.getName());
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     * 是否支持
     *
     * @param authentication 认证
     * @return 是否支持
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return NormalLoginAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
