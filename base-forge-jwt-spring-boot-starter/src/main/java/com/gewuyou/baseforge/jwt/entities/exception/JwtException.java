package com.gewuyou.baseforge.jwt.entities.exception;

import com.gewuyou.baseforge.core.exception.InternalException;

/**
 * JWT 异常
 *
 * @author gewuyou
 * @since 2024-12-31 16:41:30
 */
public class JwtException extends InternalException {
    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorMessage the detail message.
     */
    public JwtException(String errorMessage) {
        super(errorMessage);
    }
}
