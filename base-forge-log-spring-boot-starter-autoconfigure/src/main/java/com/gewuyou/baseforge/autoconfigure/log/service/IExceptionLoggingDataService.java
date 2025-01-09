package com.gewuyou.baseforge.autoconfigure.log.service;


import com.gewuyou.baseforge.entities.log.entity.ExceptionLogDataEntity;

/**
 * 异常日志数据服务接口
 *
 * @author gewuyou
 * @since 2024-11-15 17:04:28
 */
@FunctionalInterface
public interface IExceptionLoggingDataService {
    /**
     *  处理异常日志
     * @param exceptionLogDataEntity 异常日志数据实体
     */
    void processingExceptionLog(ExceptionLogDataEntity exceptionLogDataEntity);
}
