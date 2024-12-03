package com.gewuyou.security.checker;

import com.gewuyou.security.exception.AuthenticationException;
import com.gewuyou.security.i18n.enums.SecurityResponseInformation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * 默认 POST 身份验证检查
 *
 * @author gewuyou
 * @since 2024-11-28 00:27:21
 */
@Slf4j
public class DefaultPostAuthenticationChecks implements UserDetailsChecker{
    /**
     * 检查用户详细信息
     *
     * @param userDetails 用户详细信息
     */
    @Override
    public void check(UserDetails userDetails) {
        // 判断用户凭证是否过期
        if (!userDetails.isCredentialsNonExpired()) {
            log.debug("用户[{}]凭证已过期", userDetails.getUsername());
            throw new AuthenticationException(SecurityResponseInformation.ACCOUNT_CREDENTIALS_HAVE_EXPIRED);
        }
    }
}
