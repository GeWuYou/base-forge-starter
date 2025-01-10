package com.gewuyou.baseforge.security.authentication.autoconfigure.provider;

import com.gewuyou.baseforge.security.authentication.autoconfigure.service.AuthenticationUserDetailsService;
import com.gewuyou.baseforge.security.authentication.entities.exception.AuthenticationException;
import com.gewuyou.baseforge.security.authentication.entities.exception.SignInIdNotFoundException;
import com.gewuyou.baseforge.security.authentication.entities.exception.UserDetailsNotFoundException;
import com.gewuyou.baseforge.security.authentication.entities.i18n.enums.SecurityAuthenticationResponseInformation;
import com.gewuyou.baseforge.security.authentication.entities.token.NormalAuthenticationToken;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.core.userdetails.UserCache;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Objects;
import java.util.Optional;

/**
 * 普通身份验证提供程序<br/>
 * 用于处理普通身份验证，如用户名密码登录、短信验证码登录等
 *
 * @author gewuyou
 * @since 2024-12-30 15:54:35
 */
@Slf4j
public class NormalAuthenticationProvider extends AbstractAuthenticationProvider {
    /**
     * 权限映射器
     */
    private final GrantedAuthoritiesMapper authoritiesMapper;
    /**
     * 密码加密器
     */
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationUserDetailsService authenticationUserDetailsService;
    private volatile String userNotFoundEncodedPassword;

    @Setter
    private UserDetailsPasswordService userDetailsPasswordService;
    @Setter
    private CompromisedPasswordChecker compromisedPasswordChecker;

    public NormalAuthenticationProvider(UserCache userCache, GrantedAuthoritiesMapper authoritiesMapper, PasswordEncoder passwordEncoder, AuthenticationUserDetailsService authenticationUserDetailsService) {
        super(userCache);
        this.authoritiesMapper = authoritiesMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationUserDetailsService = authenticationUserDetailsService;
    }

    /**
     * 额外的身份验证检查
     *
     * @param userDetails    用户详情
     * @param authentication 登录认证令牌
     * @throws AuthenticationException 认证异常
     */
    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, Authentication authentication) throws AuthenticationException {
        Object credentials = authentication.getCredentials();
        // 密码校验
        if (Objects.isNull(credentials)) {
            log.debug("由于未提供凭据，身份验证失败");
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.PASSWORD_NOT_PROVIDED);
        }
        if (!passwordEncoder.matches((String) credentials, userDetails.getPassword())) {
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
    @Override
    protected UserDetails retrieveUser(Authentication authentication) throws AuthenticationException {
        // 准备时序攻击保护
        this.prepareTimingAttackProtection();
        try {
            // 加载用户信息
            return Optional.ofNullable(authenticationUserDetailsService
                            .loadUserBySignInId(authentication.getPrincipal()))
                    .orElseThrow(() -> new UserDetailsNotFoundException(SecurityAuthenticationResponseInformation.USER_NOT_FOUND));
        } catch (SignInIdNotFoundException e) {
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
     * 创建成功的认证
     *
     * @param principal      用户标识
     * @param authentication 认证
     * @param user           用户详情
     * @return 成功的认证
     */
    @Override
    protected Authentication createSuccessAuthentication(Object principal, Authentication authentication, UserDetails user) {
        // 获取密码
        String presentedPassword = authentication.getCredentials().toString();
        // 检查密码是否已泄露
        boolean isPasswordCompromised = Objects.nonNull(this.compromisedPasswordChecker) && this.compromisedPasswordChecker.check(presentedPassword).isCompromised();
        if (isPasswordCompromised) {
            log.debug("密码已泄露，身份验证失败");
            throw new AuthenticationException(SecurityAuthenticationResponseInformation.PASSWORD_COMPROMISED);
        }
        // 升级密码编码
        boolean upgradeEncoding = Objects.nonNull(this.userDetailsPasswordService) && this.passwordEncoder.upgradeEncoding(user.getPassword());
        if (upgradeEncoding) {
            String newPassword = this.passwordEncoder.encode(presentedPassword);
            user = this.userDetailsPasswordService.updatePassword(user, newPassword);
        }
        return getNormalLoginAuthenticationToken(principal, authentication, user);
    }

    /**
     * 获取普通登录认证令牌
     *
     * @param principal      用户唯一标识
     * @param authentication 认证
     * @param user           用户详情
     * @return 普通登录认证令牌
     */
    private NormalAuthenticationToken getNormalLoginAuthenticationToken(Object principal, Authentication authentication, UserDetails user) {
        NormalAuthenticationToken result = NormalAuthenticationToken.authenticated(
                principal,
                authentication.getCredentials(),
                this.authoritiesMapper.mapAuthorities(user.getAuthorities()));
        log.debug("身份验证成功用户: {}", result.getName());
        result.setDetails(authentication.getDetails());
        return result;
    }

    /**
     * 是否支持指定的认证类型
     *
     * @param authentication 认证类型
     * @return 是否支持
     */
    @Override
    public boolean supports(Class<?> authentication) {
        return NormalAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
