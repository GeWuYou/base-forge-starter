package com.gewuyou.i18n.exception;

import com.gewuyou.i18n.entity.ResponseInformation;

/**
 * i18n异常
 *
 * @author gewuyou
 * @since 2024-11-12 00:11:32
 */
public class I18nBaseException extends RuntimeException {
    protected final ResponseInformation responseInformation;

    public I18nBaseException(ResponseInformation responseInformation) {
        super();
        this.responseInformation = responseInformation;
    }

    public int getErrorCode() {
        return responseInformation.getResponseCode();
    }
    public String getErrorI18nMessageCode() {
        return responseInformation.getResponseI8nMessageCode();
    }

}
