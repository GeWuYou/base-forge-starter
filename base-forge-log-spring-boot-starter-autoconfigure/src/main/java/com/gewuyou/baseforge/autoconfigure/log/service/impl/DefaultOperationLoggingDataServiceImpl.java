package com.gewuyou.baseforge.autoconfigure.log.service.impl;


import com.gewuyou.baseforge.autoconfigure.log.service.IOperationLoggingDataService;
import com.gewuyou.baseforge.entities.log.entity.OperationLogDataEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * 默认操作日志数据处理实现
 *
 * @author gewuyou
 * @since 2024-11-21 15:01:33
 */
@Slf4j
public class DefaultOperationLoggingDataServiceImpl implements IOperationLoggingDataService {
    /**
     * 操作异常日志
     *
     * @param operationLogDataEntity 操作日志数据实体
     */
    @Override
    public void processingOperationLog(OperationLogDataEntity operationLogDataEntity) {
        // 打印日志在控制台
        log.info("""
                        \r=======================================
                        \r\
                        操作路径:{}\s
                        \r\
                        操作方法:{}\s
                        \r\
                        操作描述:{}\s
                        \r\
                        请求方法参数:{}\s
                        \r\
                        返回报文:{}\s
                        \r\
                        处理耗时:{} ms\s
                        \r\
                        =======================================
                        \r""",
                operationLogDataEntity.getOptUrl(),
                operationLogDataEntity.getOptMethod(),
                operationLogDataEntity.getOptDesc(),
                operationLogDataEntity.getRequestParams(),
                operationLogDataEntity.getResult(),
                operationLogDataEntity.getDuration()
        );
    }
}
