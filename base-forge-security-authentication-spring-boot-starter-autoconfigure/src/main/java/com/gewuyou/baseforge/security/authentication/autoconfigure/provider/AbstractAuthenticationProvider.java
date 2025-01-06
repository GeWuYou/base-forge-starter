package com.gewuyou.baseforge.security.authentication.autoconfigure.provider;

import com.gewuyou.baseforge.security.authentication.entities.exception.AuthenticationException;
import com.gewuyou.baseforge.security.authentication.entities.i18n.enums.SecurityAuthenticationResponseInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象身份验证提供程序<br/>
 * 用于提供身份验证服务，具体的身份验证逻辑由子类实现
 *
 * @author gewuyou
 * @since 2024-12-30 11:22:19
 */
@Slf4j
public abstract class AbstractAuthenticationProvider implements AuthenticationProvider {
    private final UserCache userCache;
    /**
     * 是否强制使用字符串作为用户的主要标识
     */
    protected boolean forcePrincipalAsString = false;

    public AbstractAuthenticationProvider(UserCache userCache) {
        this.userCache = userCache;
    }

    /**
     * 前置身份验证检查<br/>
     * 用于检查用户的身份信息是否有效，如密码是否正确，是否被锁定等
     *
     * @param userDetails 用户详情
     */
    protected void preAuthenticationCheck(UserDetails userDetails){
        if (!userDetails.isAccountNonLocked()) {
            log.warn("用户[{}]已被锁定", userDetails.getUsername());
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.ACCOUNT_IS_LOCKED);
        }
        if (!userDetails.isAccountNonLocked()) {
            log.debug("用户[{}]账户已锁定", userDetails.getUsername());
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.ACCOUNT_IS_LOCKED);
        }
        if (!userDetails.isEnabled()) {
            log.debug("用户[{}]账户已禁用", userDetails.getUsername());
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.ACCOUNT_IS_DISABLED);
        }
    }

    /**
     * 后置身份验证检查<br/>
     * 用于检查用户的身份信息是否合法，如是否被禁用等
     *
     * @param userDetails 用户详情
     */
    protected void postAuthenticationCheck(UserDetails userDetails){
        // 判断用户凭证是否过期
        if (!userDetails.isCredentialsNonExpired()) {
            log.debug("用户[{}]凭证已过期", userDetails.getUsername());
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.ACCOUNT_CREDENTIALS_HAVE_EXPIRED);
        }
    }

    /**
     * 额外的身份验证检查
     *
     * @param userDetails    用户详情
     * @param authentication 登录认证令牌
     * @throws AuthenticationException 认证异常
     */
    protected abstract void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication) throws AuthenticationException;

    /**
     * 检索用户信息
     *
     * @param authentication 登录token
     * @return 用户信息
     */
    protected abstract UserDetails retrieveUser(Authentication authentication) throws AuthenticationException;


    /**
     * 创建成功的认证
     *
     * @param principal      用户标识
     * @param user           用户详情
     * @param authentication 认证
     * @return 成功的认证
     */
    protected abstract Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user);

    /**
     * 是否支持指定的认证类型
     * @param authentication 认证类型
     * @return 是否支持
     */
    @Override
    public abstract boolean supports(Class<?> authentication);

    /**
     * 默认将用户的主要标识作为用户名
     *
     * @param authentication 登录认证令牌
     * @return 用户名
     */
    protected String getUsername(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    /**
     * 认证
     * @param authentication 登录认证令牌
     * @return 成功的认证
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AtomicBoolean databaseWasUsed = new AtomicBoolean(false);
        // 尝试从缓存中获取用户信息
        UserDetails userDetails = Optional
                .ofNullable(userCache.getUserFromCache(this.getUsername(authentication)))
                .orElseGet(() -> {
                    databaseWasUsed.set(true);
                    // 缓存中没有用户信息，从数据库中加载用户信息
                    return this.retrieveUser(authentication);
                });
        // 检查用户详情
        userDetails = checkUserDetails(authentication, userDetails, databaseWasUsed);
        // 将用户信息缓存到缓存中
        if (databaseWasUsed.get()) {
            userCache.putUserInCache(userDetails);
        }
        Object principalToReturn = forcePrincipalAsString ? userDetails.getUsername() : userDetails;
        return createSuccessAuthentication(principalToReturn, authentication, userDetails);
    }

    /**
     * 检查用户详情
     * @param authentication 登录认证令牌
     * @param userDetails 用户详情
     * @param databaseWasUsed 是否使用数据库进行身份验证
     * @return 如果检查通过，返回用户详情，否则抛出异常
     */
    private UserDetails checkUserDetails(Authentication authentication, UserDetails userDetails, AtomicBoolean databaseWasUsed) {
        try {
            // 前置身份验证检查
            this.preAuthenticationCheck(userDetails);
            // 额外的身份验证检查
            this.additionalAuthenticationChecks(userDetails, authentication);
        } catch (AuthenticationException e) {
            // 如果使用数据库进行身份验证还是失败则抛出异常
            if (databaseWasUsed.get()) {
                throw e;
            }
            // 否则从缓存中获取用户信息
            databaseWasUsed.set(true);
            userDetails = this.retrieveUser(authentication);
            // 前置身份验证检查
            this.preAuthenticationCheck(userDetails);
            // 额外的身份验证检查
            this.additionalAuthenticationChecks(userDetails, authentication);
        }
        // 后置身份验证检查
        this.postAuthenticationCheck(userDetails);
        return userDetails;
    }
}
