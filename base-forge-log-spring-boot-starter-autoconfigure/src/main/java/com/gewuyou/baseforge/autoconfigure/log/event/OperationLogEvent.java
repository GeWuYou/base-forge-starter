package com.gewuyou.baseforge.autoconfigure.log.event;



import com.gewuyou.baseforge.entities.log.entity.OperationLogProcessingObject;
import org.springframework.context.ApplicationEvent;

/**
 * 操作日志记录事件
 *
 * @author gewuyou
 * @since 2024-05-03 下午11:38:30
 */
public class OperationLogEvent extends ApplicationEvent {
    public OperationLogEvent(OperationLogProcessingObject operationLogProcessingObject) {
        super(operationLogProcessingObject);
    }
}
