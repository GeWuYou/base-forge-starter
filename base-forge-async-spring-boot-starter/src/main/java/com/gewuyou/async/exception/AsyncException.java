package com.gewuyou.async.exception;

import com.gewuyou.i18n.enums.ResponseInformation;
import com.gewuyou.i18n.exception.BaseException;
import org.springframework.context.MessageSource;

/**
 * 异步异常
 *
 * @author gewuyou
 * @since 2024-11-13 17:16:19
 */
public class AsyncException extends BaseException {
    public AsyncException(ResponseInformation responseInformation, MessageSource messageSource) {
        super(responseInformation, messageSource);
    }
}
