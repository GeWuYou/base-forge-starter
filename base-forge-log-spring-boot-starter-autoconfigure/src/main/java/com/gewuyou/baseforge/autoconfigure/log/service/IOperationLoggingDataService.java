package com.gewuyou.baseforge.autoconfigure.log.service;


import com.gewuyou.baseforge.entities.log.entity.OperationLogDataEntity;

/**
 * 操作日志数据处理接口
 *
 * @author gewuyou
 * @since 2024-11-21 14:57:09
 */
@FunctionalInterface
public interface IOperationLoggingDataService {
    /**
     *  操作异常日志
     * @param operationLogDataEntity 操作日志数据实体
     */
    void processingOperationLog(OperationLogDataEntity operationLogDataEntity);
}
