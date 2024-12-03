package com.gewuyou.security.exception;


import com.gewuyou.core.exception.GlobalException;
import com.gewuyou.i18n.entity.ResponseInformation;


/**
 * 授权异常
 *
 * @author gewuyou
 * @since 2024-11-25 17:27:40
 */
public class AuthorizationException extends GlobalException {
    public AuthorizationException(ResponseInformation responseInformation) {
        super(responseInformation);
    }
}
