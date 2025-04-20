package com.gewuyou.baseforge.autoconfigure.log.aspect;


import com.gewuyou.baseforge.autoconfigure.log.event.ExceptionLogEvent;
import com.gewuyou.baseforge.entities.log.annotation.OperationLogging;
import com.gewuyou.baseforge.entities.log.entity.ExceptionLogProcessingObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 异常日志切面
 *
 * @author gewuyou
 * @since 2024-05-04 上午12:08:56
 */
@Aspect
@Component
@Slf4j
public class ExceptionLoggingAspect {

    private final ApplicationContext applicationContext;

    @Autowired
    public ExceptionLoggingAspect(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Pointcut("execution(* com.gewuyou.*.*.controller..*.*(..))")
    public void exceptionLogPointcut() {
    }

    @AfterThrowing(value = "exceptionLogPointcut()", throwing = "e")
    private void buildAndSaveLog(JoinPoint joinPoint, Exception e) {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        // 检查方法是否有 @OperationLogging 注解
        if (method.isAnnotationPresent(OperationLogging.class)) {
            // 如果有，直接返回，不记录异常日志
            return;
        }
        try {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            HttpServletRequest request = (HttpServletRequest) Objects.requireNonNull(requestAttributes).resolveReference(RequestAttributes.REFERENCE_REQUEST);
            applicationContext.publishEvent(new ExceptionLogEvent(
                    ExceptionLogProcessingObject.
                            builder()
                            .request(request)
                            .joinPoint(joinPoint)
                            .exception(e)
                            .build()));
        } catch (Exception ex) {
            log.error("日志构建失败", ex);
        }
    }
}
