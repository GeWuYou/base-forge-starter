package com.gewuyou.security.checker;

import com.gewuyou.security.exception.AuthenticationException;
import com.gewuyou.security.i18n.enums.SecurityResponseInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 默认身份验证前检查
 *
 * @author gewuyou
 * @since 2024-11-28 00:20:18
 */
@Slf4j
public class DefaultPreAuthenticationChecks implements UserDetailsChecker{
    /**
     * 检查用户详细信息
     *
     * @param userDetails 用户详细信息
     */
    @Override
    public void check(UserDetails userDetails) {
        if(!userDetails.isAccountNonExpired()){
            log.debug("用户[{}]账户已过期", userDetails.getUsername());
            throw new AuthenticationException(SecurityResponseInformation.ACCOUNT_HAS_EXPIRED);
        }
        if(!userDetails.isAccountNonLocked()){
            log.debug("用户[{}]账户已锁定", userDetails.getUsername());
            throw new AuthenticationException(SecurityResponseInformation.ACCOUNT_IS_LOCKED);
        }
        if(!userDetails.isEnabled()){
            log.debug("用户[{}]账户已禁用", userDetails.getUsername());
            throw new AuthenticationException(SecurityResponseInformation.ACCOUNT_IS_DISABLED);
        }
    }
}
