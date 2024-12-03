package com.gewuyou.redis.exception;


import com.gewuyou.core.exception.InternalException;
import com.gewuyou.i18n.entity.InternalInformation;

/**
 * 缓存异常
 *
 * @author gewuyou
 * @since 2024-11-11 23:34:23
 */
public class CacheException extends InternalException {

    public CacheException(String errorMessage) {
        super(errorMessage);
    }

    public CacheException(String errorMessage, InternalInformation internalInformation) {
        super(errorMessage, internalInformation);
    }
}
