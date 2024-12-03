package com.gewuyou.security.exception;

import com.gewuyou.core.exception.InternalException;

/**
 * 未实现异常
 *
 * @author gewuyou
 * @since 2024-11-24 21:08:37
 */
public class NotRealizedException extends InternalException {

    public NotRealizedException(String errorMessage) {
        super(errorMessage);
    }
}
