package com.gewuyou.baseforge.entities.web.exception;


import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation;
import com.gewuyou.baseforge.autoconfigure.i18n.exception.I18nBaseException;

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
