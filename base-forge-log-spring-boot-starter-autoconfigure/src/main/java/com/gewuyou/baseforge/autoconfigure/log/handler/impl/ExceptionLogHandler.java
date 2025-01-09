package com.gewuyou.baseforge.autoconfigure.log.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.autoconfigure.log.handler.IExceptionLogHandler;
import com.gewuyou.baseforge.autoconfigure.log.service.IExceptionLoggingDataService;
import com.gewuyou.baseforge.autoconfigure.util.ExceptionUtil;
import com.gewuyou.baseforge.autoconfigure.util.IpUtil;
import com.gewuyou.baseforge.entities.log.entity.ExceptionLogDataEntity;
import com.gewuyou.baseforge.entities.log.entity.ExceptionLogProcessingObject;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * 异常日志处理程序
 *
 * @author gewuyou
 * @since 2024-11-15 16:06:44
 */
@Slf4j
public class ExceptionLogHandler implements IExceptionLogHandler {
    protected final IExceptionLoggingDataService exceptionLogDataService;
    protected final ObjectMapper objectMapper;

    public ExceptionLogHandler(
            IExceptionLoggingDataService exceptionLogDataService,
            ObjectMapper objectMapper
    ) {
        this.exceptionLogDataService = exceptionLogDataService;
        this.objectMapper = objectMapper;
    }
    /**
     * 处理异常日志
     *
     * @param exceptionLogProcessingObject 异常日志处理对象
     */
    @Override
    public void handle(ExceptionLogProcessingObject exceptionLogProcessingObject) {
        HttpServletRequest request = exceptionLogProcessingObject.getRequest();
        JoinPoint joinPoint = exceptionLogProcessingObject.getJoinPoint();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String ipAddress = IpUtil.getIpAddress(Objects.requireNonNull(request));
        String className = joinPoint.getTarget().getClass().getName();
        String methodName = method.getName();
        String optMethod = className + "." + methodName;
        Operation operation = method.getAnnotation(Operation.class);
        Exception exception = exceptionLogProcessingObject.getException();
        try {
            exceptionLogDataService.processingExceptionLog(
                    ExceptionLogDataEntity
                            .builder()
                            .optUrl(request.getRequestURI())
                            .optMethod(optMethod)
                            .requestMethod(request.getMethod())
                            .requestParams(objectMapper.writeValueAsString(joinPoint.getArgs()))
                            .optDesc(operation.description())
                            .exceptionName(exception.getClass().getName())
                            .exceptionInfo(ExceptionUtil.getTrace(exception))
                            .ipAddress(ipAddress)
                            .build()
            );
        } catch (JsonProcessingException e) {
            log.error("日志处理异常", e);
        }
    }
}
