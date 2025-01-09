package com.gewuyou.baseforge.autoconfigure.log.event;



import com.gewuyou.baseforge.entities.log.entity.ExceptionLogProcessingObject;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * 异常日志事件
 *
 * @author gewuyou
 * @since 2024-05-04 上午12:23:35
 */
@Getter
public class ExceptionLogEvent extends ApplicationEvent {
    public ExceptionLogEvent(ExceptionLogProcessingObject exceptionLogProcessingObject) {
        super(exceptionLogProcessingObject);
    }
}
