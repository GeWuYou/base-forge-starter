package com.gewuyou.baseforge.autoconfigure.log.handler;


import com.gewuyou.baseforge.entities.log.entity.OperationLogProcessingObject;

/**
 * 操作日志处理程序接口
 *
 * @author gewuyou
 * @since 2024-11-21 15:27:28
 */
@FunctionalInterface
public interface IOperationLogHandler {
    /**
     * 处理操作日志
     * @param operationLogProcessingObject 操作日志处理对象
     */
    void handle(OperationLogProcessingObject operationLogProcessingObject);
}
