package com.gewuyou.i18n.exception;

import com.gewuyou.i18n.enums.ResponseInformation;

/**
 * 基本异常
 *
 * @author gewuyou
 * @since 2024-11-12 00:11:32
 */
public class I18nBaseException extends RuntimeException {
    protected final ResponseInformation responseInformation;

    public I18nBaseException(ResponseInformation responseInformation) {
        super(responseInformation.getMessage());
        this.responseInformation = responseInformation;
    }

    public int getErrorCode() {
        return responseInformation.getCode();
    }
    public String getErrorMessageKey() {
        return responseInformation.getMessage();
    }

}
