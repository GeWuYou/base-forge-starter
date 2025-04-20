package com.gewuyou.baseforge.entities.log.annotation;






import com.gewuyou.baseforge.entities.log.enums.LoggingLevel;
import com.gewuyou.baseforge.entities.log.enums.OperationType;

import java.lang.annotation.*;

/**
 * 操作日志注解
 *
 * @author gewuyou
 * @since 2024-04-13 下午4:40:35
 */
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Target({ElementType.METHOD})
public @interface OperationLogging {
    /**
     * 设定默认的日志级别
     *
     * @return Info
     */
    LoggingLevel level() default LoggingLevel.INFO;

    /**
     * 是否记录参数
     *
     * @return true
     */
    boolean logParams() default true;

    /**
     * 是否记录返回结果
     *
     * @return true
     */
    boolean logResult() default true;

    /**
     * 操作类型
     *
     * @return 默认：OTHER
     */
    OperationType type() default OperationType.OTHER;

    /**
     * 排除参数(仅在logParams=true时有效)
     * @return 默认：空数组
     */
    String[] exclude() default {};
}
