package com.gewuyou.baseforge.autoconfigure.log.handler;


import com.gewuyou.baseforge.entities.log.entity.ExceptionLogProcessingObject;

/**
 * 异常日志处理程序
 *
 * @author gewuyou
 * @since 2024-11-15 15:36:55
 */
@FunctionalInterface
public interface IExceptionLogHandler {
    /**
     * 处理异常日志
     * @param exceptionLogProcessingObject 异常日志处理对象
     */
    void handle(ExceptionLogProcessingObject exceptionLogProcessingObject);
}
