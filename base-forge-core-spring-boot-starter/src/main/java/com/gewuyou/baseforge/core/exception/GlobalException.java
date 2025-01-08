package com.gewuyou.baseforge.core.exception;


import com.gewuyou.baseforge.autoconfigure.i18n.entity.ResponseInformation;
import com.gewuyou.baseforge.autoconfigure.i18n.exception.I18nBaseException;

/**
 * 全局异常
 *
 * @author gewuyou
 * @since 2024-11-23 16:45:10
 */
public class GlobalException extends I18nBaseException {
    public GlobalException(ResponseInformation responseInformation) {
        super(responseInformation);
    }
}
