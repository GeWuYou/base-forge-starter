package com.gewuyou.util.exception;

import com.gewuyou.core.exception.InternalException;

/**
 *工具异常
 *
 * @since 2024-11-24 21:54:05
 * @author gewuyou
 */
public class UtilException extends InternalException {

    public UtilException(String errorMessage) {
        super(errorMessage);
    }
}
