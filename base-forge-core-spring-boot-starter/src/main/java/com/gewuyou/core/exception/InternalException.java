package com.gewuyou.core.exception;

import com.gewuyou.i18n.entity.InternalInformation;
import lombok.Getter;

/**
 * 内部异常
 *
 * @author gewuyou
 * @since 2024-11-24 21:14:03
 */
@Getter
public class InternalException extends RuntimeException {
    private final String errorMessage;
    /**
     * 可选(国际化内部错误信息码)
     */
    private InternalInformation internalInformation;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     */
    public InternalException(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public InternalException(String errorMessage,InternalInformation internalInformation) {
        this.errorMessage = errorMessage;
        this.internalInformation = internalInformation;
    }
}
