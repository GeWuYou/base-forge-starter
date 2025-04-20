package com.gewuyou.baseforge.security.authentication.autoconfigure.provider;

import com.gewuyou.baseforge.security.authentication.autoconfigure.service.UserCacheService;
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.UserDetailsPasswordService;
import com.gewuyou.baseforge.security.authentication.autoconfigure.service.UserDetailsService;
import com.gewuyou.baseforge.security.authentication.entities.entity.UserDetails;
import com.gewuyou.baseforge.security.authentication.entities.exception.AuthenticationException;
import com.gewuyou.baseforge.security.authentication.entities.exception.PrincipalNotFoundException;
import com.gewuyou.baseforge.security.authentication.entities.i18n.enums.SecurityAuthenticationResponseInformation;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 抽象身份验证提供程序<br/>
 * 用于提供身份验证服务，具体的身份验证逻辑由子类实现
 * 如果想要实现多身份验证提供程序，可以继承该类，并实现其中的方法
 *
 * @author gewuyou
 * @since 2024-12-30 11:22:19
 */
@Slf4j
public abstract class AbstractPrincipalPasswordAuthenticationProvider implements AuthenticationProvider {
    private final UserCacheService userCacheService;
    private final UserDetailsService userDetailsService;
    /**
     * 密码加密器
     */
    private final PasswordEncoder passwordEncoder;
    private UserDetailsPasswordService userDetailsPasswordService;
    private CompromisedPasswordChecker compromisedPasswordChecker;
    @Setter
    private volatile String userNotFoundEncodedPassword;

    protected AbstractPrincipalPasswordAuthenticationProvider(UserCacheService userCacheService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder) {
        this.userCacheService = userCacheService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    protected AbstractPrincipalPasswordAuthenticationProvider(UserCacheService userCacheService, UserDetailsService userDetailsService, PasswordEncoder passwordEncoder, UserDetailsPasswordService userDetailsPasswordService, CompromisedPasswordChecker compromisedPasswordChecker) {
        this.userCacheService = userCacheService;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsPasswordService = userDetailsPasswordService;
        this.compromisedPasswordChecker = compromisedPasswordChecker;
    }

    /**
     * 前置身份验证检查<br/>
     * 用于检查用户的身份信息是否有效，如密码是否正确，是否被锁定等
     *
     * @param userDetails 用户详情
     */
    protected void preAuthenticationCheck(UserDetails userDetails) {
        // 获取唯一标识
        val userOnlyIdentity = userDetails.getUserOnlyIdentity();
        if (!userDetails.isAccountNonLocked()) {
            log.warn("用户[{}]已被锁定,唯一标识[{}]", userDetails.getPrincipal(), userOnlyIdentity);
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.ACCOUNT_IS_LOCKED);
        }
        if (!userDetails.isEnabled()) {
            log.debug("用户[{}]账户已禁用,唯一标识[{}]", userDetails.getPrincipal(), userOnlyIdentity);
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.ACCOUNT_IS_DISABLED);
        }
    }

    /**
     * 后置身份验证检查<br/>
     * 用于检查用户的身份信息是否合法，如是否被禁用等
     *
     * @param userDetails 用户详情
     */
    protected void postAuthenticationCheck(UserDetails userDetails) {
        // 判断用户凭证是否过期
        if (!userDetails.isCredentialsNonExpired()) {
            log.debug("用户[{}]凭证已过期,唯一标识[{}]", userDetails.getPrincipal(), userDetails.getUserOnlyIdentity());
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.ACCOUNT_CREDENTIALS_HAVE_EXPIRED);
        }
    }

    /**
     * 额外的身份验证检查
     *
     * @param userDetails    用户详情
     * @param authentication 登录认证令牌
     * @throws AuthenticationException 认证异常
     * @apiNote 子类可以重写该方法，实现额外的身份验证逻辑 通常在此方法中，对用户的密码进行验证
     */
    protected void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication) throws AuthenticationException {
        Object credentials = authentication.getCredentials();
        // 密码校验
        if (Objects.isNull(credentials)) {
            log.debug("由于未提供凭据，身份验证失败");
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.PASSWORD_NOT_PROVIDED);
        }
        if (!passwordEncoder.matches((String) credentials, (String) userDetails.getCredentials())) {
            log.debug("密码不匹配，身份验证失败");
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.PASSWORD_NOT_MATCH);
        }
    }

    /**
     * 检索用户信息
     *
     * @param authentication 登录token
     * @return 用户信息
     */
    protected UserDetails retrieveUser(Authentication authentication) {
        // 准备时序攻击保护
        this.prepareTimingAttackProtection();
        try {
            if (Objects.isNull(authentication.getPrincipal())) {
                throw new PrincipalNotFoundException(SecurityAuthenticationResponseInformation.PRINCIPAL_NOT_PROVIDED);
            }
            // 加载用户信息
            return userDetailsService.loadUserByPrincipal(authentication.getPrincipal());
        } catch (PrincipalNotFoundException e) {
            // 启用时序攻击保护
            this.mitigateAgainstTimingAttack(authentication);
            throw e;
        } catch (Exception e) {
            log.error("加载用户信息失败", e);
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * 准备时序攻击保护
     */
    private void prepareTimingAttackProtection() {
        if (Objects.isNull(this.userNotFoundEncodedPassword)) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(RandomStringUtils.insecure().next(RandomUtils.insecure().randomInt(20, 32)));
        }
    }

    /**
     * 防止时序攻击
     *
     * @param authentication 登录token
     */
    private void mitigateAgainstTimingAttack(Authentication authentication) {
        if (Objects.nonNull(authentication.getCredentials())) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }

    /**
     * 处理密码
     *
     * @param authentication 登录认证令牌
     * @param user           用户详情
     * @apiNote 子类可以重写该方法，实现密码的处理逻辑 比如检测密码是否泄露，升级密码编码等
     */
    protected UserDetails handlePassword(Authentication authentication, UserDetails user) {
        // 获取密码
        String presentedPassword = authentication.getCredentials().toString();
        // 检查密码是否已泄露
        boolean isPasswordCompromised = Objects.nonNull(this.compromisedPasswordChecker) && this.compromisedPasswordChecker.check(presentedPassword).isCompromised();
        if (isPasswordCompromised) {
            log.debug("密码已泄露，身份验证失败");
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.PASSWORD_COMPROMISED);
        }
        // 升级密码编码
        boolean upgradeEncoding = Objects.nonNull(this.userDetailsPasswordService) && this.passwordEncoder.upgradeEncoding(user.getCredentials().toString());
        if (upgradeEncoding) {
            String newPassword = this.passwordEncoder.encode(presentedPassword);
            return this.userDetailsPasswordService.updatePassword(user, newPassword);
        }
        return user;
    }

    /**
     * 创建成功的认证
     *
     * @param user           用户详情
     * @param authentication 认证
     * @return 成功的认证
     */
    protected abstract Authentication createSuccessAuthentication(Authentication authentication, UserDetails user);

    /**
     * 获取用户登录标识
     *
     * @param authentication 登录认证令牌
     * @return 用户名
     */
    protected String getPrincipal(Authentication authentication) {
        return authentication.getPrincipal().toString();
    }

    /**
     * 认证
     *
     * @param authentication 登录认证令牌
     * @return 成功的认证
     * @throws AuthenticationException 认证异常
     */
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        AtomicBoolean databaseWasUsed = new AtomicBoolean(false);
        // 尝试从缓存中获取用户信息
        UserDetails userDetails = Optional
                .ofNullable(userCacheService.getUserFromCache(this.getPrincipal(authentication)))
                .orElseGet(() -> {
                    databaseWasUsed.set(true);
                    // 缓存中没有用户信息，从数据库中加载用户信息
                    return this.retrieveUser(authentication);
                });
        // 检查用户详情
        userDetails = checkUserDetails(authentication, userDetails, databaseWasUsed);
        // 将用户信息缓存到缓存中
        if (databaseWasUsed.get()) {
            userCacheService.putUserToCache(userDetails);
        }
        userDetails = handlePassword(authentication, userDetails);
        return createSuccessAuthentication(authentication, userDetails);
    }

    /**
     * 检查用户详情
     *
     * @param authentication  登录认证令牌
     * @param userDetails     用户详情
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
