package com.gewuyou.baseforge.autoconfigure.i18n.exception;

import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation;

/**
 * i18n异常
 *
 * @author gewuyou
 * @since 2024-11-12 00:11:32
 */
public class I18nBaseException extends RuntimeException {
    protected final transient ResponseInformation responseInformation;

    public I18nBaseException(ResponseInformation responseInformation) {
        super();
        this.responseInformation = responseInformation;
    }
    public I18nBaseException(ResponseInformation responseInformation, Throwable cause) {
        super(cause);
        this.responseInformation = responseInformation;
    }

    public int getErrorCode() {
        return responseInformation.getResponseCode();
    }
    public String getErrorI18nMessageCode() {
        return responseInformation.getResponseI8nMessageCode();
    }

}
