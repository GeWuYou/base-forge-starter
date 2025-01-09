package com.gewuyou.baseforge.autoconfigure.log.aspect;


import com.gewuyou.baseforge.autoconfigure.log.event.OperationLogEvent;
import com.gewuyou.baseforge.entities.log.annotation.OperationLogging;
import com.gewuyou.baseforge.entities.log.entity.OperationLogProcessingObject;
import com.gewuyou.baseforge.entities.log.enums.LoggingLevel;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 操作日志记录切面
 *
 * @author gewuyou
 * @since 2024-05-03 下午10:00:37
 */
@Aspect
@Component
@Slf4j
public class OperationLoggingAspect {

    private final ApplicationContext applicationContext;

    private final ThreadLocal<LocalDateTime> timer = new ThreadLocal<>();

    @Autowired
    public OperationLoggingAspect(
            ApplicationContext applicationContext
    ) {
        this.applicationContext = applicationContext;
    }

    @Before("@annotation(operationLogging)")
    public void before(OperationLogging operationLogging) {
        // 记录开始时间
        timer.set(LocalDateTime.now());
    }

    @Around("@annotation(operationLogging)")
    public Object around(ProceedingJoinPoint joinPoint, OperationLogging operationLogging) throws Throwable {
        // 获取注解中的日志级别
        LoggingLevel level = operationLogging.level();
        try {
            logMessage(level, "开始执行操作: " + joinPoint.getSignature());
            // 执行方法
            Object result = joinPoint.proceed();
            logMessage(level, "方法执行成功: " + joinPoint.getSignature());
            return result;
        } catch (Throwable e) {
            // 记录异常日志
            log.error("方法执行异常：{}", joinPoint.getSignature(), e);
            throw e;
        }
    }

    @AfterReturning(pointcut = "@annotation(operationLogging)", returning = "result")
    public void afterReturning(JoinPoint joinPoint, OperationLogging operationLogging, Object result) {
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);
            // 获取开始时间
            LocalDateTime startTime = timer.get();
            // 计算耗时
            long duration = Duration.between(startTime, LocalDateTime.now()).toMillis();
            // 保存日志
            applicationContext
                    .publishEvent(new OperationLogEvent(OperationLogProcessingObject
                            .builder()
                            .joinPoint(joinPoint)
                            .operationLogging(operationLogging)
                            .result(result)
                            .request(request)
                            .duration(duration)
                            .build()));
        } catch (Exception e) {
            log.error("操作日志记录失败", e);
        } finally {
            // 清除计时器
            timer.remove();
        }
    }

    /**
     * 输出日志信息
     *
     * @param level   日志级别
     * @param message 信息
     */
    private void logMessage(LoggingLevel level, String message) {
        switch (level) {
            case TRACE -> log.trace(message);
            case DEBUG -> log.debug(message);
            case INFO -> log.info(message);
            case WARN -> log.warn(message);
            case ERROR -> log.error(message);
        }
    }
}
