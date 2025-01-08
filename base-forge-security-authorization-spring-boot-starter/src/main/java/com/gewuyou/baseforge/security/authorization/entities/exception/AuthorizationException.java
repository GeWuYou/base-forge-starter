package com.gewuyou.baseforge.security.authorization.entities.exception;

import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation;
import com.gewuyou.baseforge.core.exception.GlobalException;

/**
 * 授权异常
 *
 * @author gewuyou
 * @since 2025-01-06 15:30:26
 */
public class AuthorizationException extends GlobalException {
    public AuthorizationException(ResponseInformation responseInformation) {
        super(responseInformation);
    }
}
