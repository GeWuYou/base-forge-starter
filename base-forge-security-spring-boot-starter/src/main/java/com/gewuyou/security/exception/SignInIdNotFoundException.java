package com.gewuyou.security.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * 登录 ID 未找到异常
 *
 * @author gewuyou
 * @since 2024-11-05 19:38:51
 */
public class SignInIdNotFoundException extends AuthenticationException {
    public SignInIdNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public SignInIdNotFoundException(String msg) {
        super(msg);
    }
}
