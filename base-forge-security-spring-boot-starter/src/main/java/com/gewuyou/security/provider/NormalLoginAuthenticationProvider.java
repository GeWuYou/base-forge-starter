package com.gewuyou.security.provider;

import com.gewuyou.security.checker.UserDetailsChecker;
import com.gewuyou.security.exception.AuthenticationException;
import com.gewuyou.security.exception.SignInIdNotFoundException;
import com.gewuyou.security.i18n.enums.SecurityResponseInformation;
import com.gewuyou.security.service.IUserDetailsService;
import com.gewuyou.security.token.NormalLoginAuthenticationToken;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.authentication.password.CompromisedPasswordException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;


/**
 * 普通登录身份验证提供程序(支持用户名、手机号、邮箱与密码登录)
 *
 * @author gewuyou
 * @since 2024-11-04 17:49:48
 */
@Slf4j
public class NormalLoginAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {
    /**
     * 用户不存在密码
     */
    private static final String USER_NOT_FOUND_PASSWORD = "userNotFoundPassword";
    private volatile String userNotFoundEncodedPassword;
    @Setter
    private PasswordEncoder passwordEncoder;
    @Setter
    private IUserDetailsService userDetailsService;
    @Setter
    private UserDetailsPasswordService userDetailsPasswordService;
    @Setter
    private CompromisedPasswordChecker compromisedPasswordChecker;

    public NormalLoginAuthenticationProvider(
            UserDetailsChecker preAuthenticationChecks,
            UserDetailsChecker postAuthenticationChecks,
            UserCache userCache,
            PasswordEncoder passwordEncoder,
            IUserDetailsService userDetailsService
    ) {
        super(preAuthenticationChecks, postAuthenticationChecks,userCache);
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
    }


    /**
     * 额外的身份验证检查
     *
     * @param userDetails    用户详情
     * @param authentication 登录认证令牌
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, NormalLoginAuthenticationToken authentication) {
        // 校验密码
        if (Objects.isNull(authentication.getCredentials())) {
            log.debug("由于未提供凭据，身份验证失败");
            throw new AuthenticationException(SecurityResponseInformation.PASSWORD_NOT_PROVIDED);
        } else {
            String presentedPassword = authentication.getCredentials().toString();
            if (!passwordEncoder.matches(presentedPassword, userDetails.getPassword())) {
                log.debug("由于密码与存储的值不匹配，身份验证失败");
                throw new AuthenticationException(SecurityResponseInformation.PASSWORD_NOT_MATCH);
            }
        }
    }

    /**
     * 检索用户信息
     *
     * @param signInId       用户登录ID(用户名 or 手机号 or 邮箱)
     * @param authentication 登录token
     * @return 用户信息
     */
    @Override
    protected UserDetails retrieveUser(String signInId, NormalLoginAuthenticationToken authentication) {
        // 准备时序攻击保护
        this.prepareTimingAttackProtection();
        try {
            // 加载用户信息
            return loadUser(signInId);
        } catch (SignInIdNotFoundException e) {
            // 启用时序攻击保护
            this.mitigateAgainstTimingAttack(authentication);
            throw e;
        } catch (InternalAuthenticationServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new InternalAuthenticationServiceException(e.getMessage(), e);
        }
    }

    /**
     * 加载用户信息
     * @param signInId 登录ID
     * @return 用户信息
     */
    private UserDetails loadUser(String signInId) {
        UserDetails loadedUser = userDetailsService.loadUserBySignInId(signInId);
        if (Objects.isNull(loadedUser)) {
            throw new InternalAuthenticationServiceException("UserDetailsService returned null, which is an interface contract violation");
        } else {
            return loadedUser;
        }
    }

    /**
     * 创建成功的认证
     *
     * @param principal      用户标识
     * @param authentication 认证
     * @param user           用户详情
     * @return 成功的认证
     */
    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        String presentedPassword = authentication.getCredentials().toString();
        // 检查密码是否已泄露
        boolean isPasswordCompromised = Objects.nonNull(this.compromisedPasswordChecker) && this.compromisedPasswordChecker.check(presentedPassword).isCompromised();
        if (isPasswordCompromised) {
            throw new CompromisedPasswordException("The provided password is compromised, please change your password");
        } else {
            // 升级密码编码
            boolean upgradeEncoding = Objects.nonNull(this.userDetailsPasswordService) && this.passwordEncoder.upgradeEncoding(user.getPassword());
            if (upgradeEncoding) {
                String newPassword = this.passwordEncoder.encode(presentedPassword);
                user = this.userDetailsPasswordService.updatePassword(user, newPassword);
            }

            return super.createSuccessAuthentication(principal, authentication, user);
        }
    }

    /**
     * 准备时序攻击保护
     */
    private void prepareTimingAttackProtection() {
        if (Objects.isNull(this.userNotFoundEncodedPassword)) {
            this.userNotFoundEncodedPassword = this.passwordEncoder.encode(USER_NOT_FOUND_PASSWORD);
        }
    }

    /**
     * 防止时序攻击
     *
     * @param authentication 登录token
     */
    private void mitigateAgainstTimingAttack(NormalLoginAuthenticationToken authentication) {
        if (Objects.nonNull(authentication.getCredentials())) {
            String presentedPassword = authentication.getCredentials().toString();
            this.passwordEncoder.matches(presentedPassword, this.userNotFoundEncodedPassword);
        }
    }
    protected IUserDetailsService getUserDetailsService() {
        return this.userDetailsService;
    }
    protected PasswordEncoder getPasswordEncoder() {
        return this.passwordEncoder;
    }
}
