package com.gewuyou.web.exception;

import com.gewuyou.i18n.enums.ResponseInformation;
import com.gewuyou.i18n.exception.I18nBaseException;

/**
 * 访问异常
 *
 * @author gewuyou
 * @since 2024-11-12 16:51:06
 */
public class AccessException extends I18nBaseException {

    public AccessException(ResponseInformation responseInformation) {
        super(responseInformation);
    }
}
