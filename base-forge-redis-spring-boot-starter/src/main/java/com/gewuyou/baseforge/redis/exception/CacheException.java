package com.gewuyou.baseforge.redis.exception;



import com.gewuyou.baseforge.autoconfigure.i18n.entity.InternalInformation;
import com.gewuyou.baseforge.core.exception.InternalException;


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
