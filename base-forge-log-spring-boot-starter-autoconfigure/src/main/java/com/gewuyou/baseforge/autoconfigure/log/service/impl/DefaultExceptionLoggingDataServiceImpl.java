package com.gewuyou.baseforge.autoconfigure.log.service.impl;


import com.gewuyou.baseforge.autoconfigure.log.service.IExceptionLoggingDataService;
import com.gewuyou.baseforge.entities.log.entity.ExceptionLogDataEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * 异常日志数据服务默认实现
 *
 * @author gewuyou
 * @since 2024-11-17 19:29:30
 */
@Slf4j
public class DefaultExceptionLoggingDataServiceImpl implements IExceptionLoggingDataService {
    /**
     * 操作异常日志
     *
     * @param exceptionLogDataEntity 异常日志数据实体
     */
    @Override
    public void processingExceptionLog(ExceptionLogDataEntity exceptionLogDataEntity) {
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
                        请求方法:{}\s
                        \r\
                        请求参数:{}\s
                        \r\
                        ip地址:{}  \s
                        \r\
                        异常名称:{}\s
                        \r\
                        =======================================
                        \r""",
                exceptionLogDataEntity.getOptUrl(),
                exceptionLogDataEntity.getOptMethod(),
                exceptionLogDataEntity.getOptDesc(),
                exceptionLogDataEntity.getRequestMethod(),
                exceptionLogDataEntity.getRequestParams(),
                exceptionLogDataEntity.getIpAddress(),
                exceptionLogDataEntity.getExceptionName()
        );
    }
}
