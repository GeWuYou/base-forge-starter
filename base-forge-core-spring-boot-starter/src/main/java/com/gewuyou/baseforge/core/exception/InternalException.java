package com.gewuyou.baseforge.core.exception;


import com.gewuyou.baseforge.autoconfigure.i18n.entity.InternalInformation;
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
    private final transient InternalInformation internalInformation;

    /**
     * Constructs a new runtime exception with {@code null} as its
     * detail message.  The cause is not initialized, and may subsequently be
     * initialized by a call to {@link #initCause}.
     *
     * @param errorMessage the detail message.
     */
    public InternalException(String errorMessage) {
        this.errorMessage = errorMessage;
        this.internalInformation = null;
    }

    public InternalException(String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
        this.internalInformation = null;
    }

    public InternalException(String errorMessage, InternalInformation internalInformation) {
        this.errorMessage = errorMessage;
        this.internalInformation = internalInformation;
    }

    public InternalException(String errorMessage, Throwable cause, InternalInformation internalInformation) {
        super(errorMessage, cause);
        this.errorMessage = errorMessage;
        this.internalInformation = internalInformation;
    }
}
