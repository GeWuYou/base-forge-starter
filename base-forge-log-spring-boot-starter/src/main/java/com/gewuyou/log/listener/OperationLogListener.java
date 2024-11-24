package com.gewuyou.log.listener;


import com.gewuyou.log.entity.OperationLogProcessingObject;
import com.gewuyou.log.event.OperationLogEvent;
import com.gewuyou.log.handler.IOperationLogHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;

/**
 * 操作日志监听器
 *
 * @author gewuyou
 * @since 2024-05-03 下午11:40:07
 */
public class OperationLogListener implements ApplicationListener<OperationLogEvent> {
    private final IOperationLogHandler operationLogHandler;

    @Autowired
    public OperationLogListener(IOperationLogHandler operationLogHandler) {
        this.operationLogHandler = operationLogHandler;
    }

    /**
     * Handle an application event.
     *
     * @param operationLogEvent the event to respond to
     */
    @Async
    @Override
    public void onApplicationEvent(@NonNull OperationLogEvent operationLogEvent) {
        operationLogHandler.handle((OperationLogProcessingObject) operationLogEvent.getSource());
    }
}
