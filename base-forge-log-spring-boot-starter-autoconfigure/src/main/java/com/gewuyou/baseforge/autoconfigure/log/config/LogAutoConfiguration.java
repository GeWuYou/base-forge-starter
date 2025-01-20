package com.gewuyou.baseforge.autoconfigure.log.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.autoconfigure.log.aspect.ExceptionLoggingAspect;
import com.gewuyou.baseforge.autoconfigure.log.aspect.OperationLoggingAspect;
import com.gewuyou.baseforge.autoconfigure.log.handler.IExceptionLogHandler;
import com.gewuyou.baseforge.autoconfigure.log.handler.IOperationLogHandler;
import com.gewuyou.baseforge.autoconfigure.log.handler.impl.ExceptionLogHandler;
import com.gewuyou.baseforge.autoconfigure.log.handler.impl.OperationLogHandler;
import com.gewuyou.baseforge.autoconfigure.log.listener.ExceptionLogListener;
import com.gewuyou.baseforge.autoconfigure.log.listener.OperationLogListener;
import com.gewuyou.baseforge.autoconfigure.log.service.IExceptionLoggingDataService;
import com.gewuyou.baseforge.autoconfigure.log.service.IOperationLoggingDataService;
import com.gewuyou.baseforge.autoconfigure.log.service.impl.DefaultExceptionLoggingDataServiceImpl;
import com.gewuyou.baseforge.autoconfigure.log.service.impl.DefaultOperationLoggingDataServiceImpl;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 日志自动配置类
 *
 * @author gewuyou
 * @since 2024-11-13 17:55:36
 */
@Configuration
public class LogAutoConfiguration {

    /**
     * 默认的异常日志数据服务实现类
     * @return {@link DefaultExceptionLoggingDataServiceImpl}
     */
    @Bean
    @ConditionalOnMissingBean(IExceptionLoggingDataService.class)
    public IExceptionLoggingDataService createExceptionLogDataService() {
        return new DefaultExceptionLoggingDataServiceImpl();
    }

    /**
     * 默认的操作日志数据服务实现类
     * @return {@link DefaultOperationLoggingDataServiceImpl}
     */
    @Bean
    @ConditionalOnMissingBean(IOperationLoggingDataService.class)
    public IOperationLoggingDataService createOperationLoggingDataService() {
        return new DefaultOperationLoggingDataServiceImpl();
    }

    /**
     * 默认的异常日志处理器
     * @param exceptionLogDataService 异常日志数据服务
     * @param objectMapper 对象映射器
     * @return {@link ExceptionLogHandler}
     */
    @Bean
    @ConditionalOnMissingBean(IExceptionLogHandler.class)
    public IExceptionLogHandler createExceptionLogHandler(IExceptionLoggingDataService exceptionLogDataService, ObjectMapper objectMapper) {
        return new ExceptionLogHandler(exceptionLogDataService,objectMapper);
    }

    /**
     * 默认的操作日志处理器
     * @param operationLogDataService 操作日志数据服务
     * @param objectMapper 对象映射器
     * @return {@link OperationLogHandler}
     */
    @Bean
    @ConditionalOnMissingBean(IOperationLogHandler.class)
    public IOperationLogHandler createOperationLogHandler(IOperationLoggingDataService operationLogDataService, ObjectMapper objectMapper) {
        return new OperationLogHandler(operationLogDataService,objectMapper);
    }

    /**
     * 默认的异常日志监听器
     * @param exceptionLogHandler 异常日志处理器
     * @return {@link ExceptionLogListener}
     */
    @Bean
    public ExceptionLogListener createExceptionLogListener(IExceptionLogHandler exceptionLogHandler) {
        return new ExceptionLogListener(exceptionLogHandler);
    }

    /**
     * 默认的操作日志监听器
     * @param operationLogHandler 操作日志处理器
     * @return {@link OperationLogListener}
     */
    @Bean
    public OperationLogListener createOperationLogListener(IOperationLogHandler operationLogHandler) {
        return new OperationLogListener(operationLogHandler);
    }

    /**
     * 默认的异常日志切面
     * @param applicationContext 应用上下文
     * @return {@link ExceptionLoggingAspect}
     */
    @Bean
    public ExceptionLoggingAspect createExceptionLogAspect(ApplicationContext applicationContext) {
        return new ExceptionLoggingAspect(applicationContext);
    }

    /**
     * 默认的操作日志切面
     * @param applicationContext 应用上下文
     * @return {@link OperationLoggingAspect}
     */
    @Bean
    public OperationLoggingAspect createOperationLogAspect(ApplicationContext applicationContext) {
        return new OperationLoggingAspect(applicationContext);
    }
}
