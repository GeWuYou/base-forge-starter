package com.gewuyou.baseforge.autoconfigure.log.listener;



import com.gewuyou.baseforge.autoconfigure.log.event.ExceptionLogEvent;
import com.gewuyou.baseforge.autoconfigure.log.handler.IExceptionLogHandler;
import com.gewuyou.baseforge.entities.log.entity.ExceptionLogProcessingObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.lang.NonNull;
import org.springframework.scheduling.annotation.Async;

/**
 * 异常日志监听器
 *
 * @author gewuyou
 * @since 2024-05-04 上午12:25:38
 */
public class ExceptionLogListener implements ApplicationListener<ExceptionLogEvent> {
    private final IExceptionLogHandler exceptionLogHandler;

    @Autowired
    public ExceptionLogListener(IExceptionLogHandler exceptionLogHandler) {
        this.exceptionLogHandler = exceptionLogHandler;
    }

    /**
     * Handle an application event.
     *
     * @param exceptionLogEvent the event to respond to
     */
    @Async
    @Override
    public void onApplicationEvent(@NonNull ExceptionLogEvent exceptionLogEvent) {
        exceptionLogHandler.handle((ExceptionLogProcessingObject) exceptionLogEvent.getSource());
    }
}
