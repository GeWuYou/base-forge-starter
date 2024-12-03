package com.gewuyou.security.exception;

import com.gewuyou.core.exception.GlobalException;
import com.gewuyou.i18n.entity.ResponseInformation;

/**
 *认证异常
 *
 * @since 2024-11-27 23:59:29
 * @author gewuyou
 */
public class AuthenticationException extends GlobalException {
    public AuthenticationException(ResponseInformation responseInformation) {
        super(responseInformation);
    }
}
