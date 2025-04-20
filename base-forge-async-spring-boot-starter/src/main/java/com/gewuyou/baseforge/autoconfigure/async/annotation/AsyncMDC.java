package com.gewuyou.baseforge.autoconfigure.async.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 异步 MDC
 *
 * @author gewuyou
 * @since 2025-01-22 14:45:32
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AsyncMDC {
}
