package com.gewuyou.redis.exception;


import com.gewuyou.i18n.enums.ResponseInformation;
import com.gewuyou.i18n.exception.BaseException;
import org.springframework.context.MessageSource;

/**
 * 缓存异常
 *
 * @author gewuyou
 * @since 2024-11-11 23:34:23
 */
public class CacheException extends BaseException {
    public CacheException(ResponseInformation responseInformation, MessageSource messageSource) {
        super(responseInformation, messageSource);
    }
}
