package com.gewuyou.baseforge.autoconfigure.log.handler.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.autoconfigure.log.handler.IOperationLogHandler;
import com.gewuyou.baseforge.autoconfigure.log.service.IOperationLoggingDataService;
import com.gewuyou.baseforge.autoconfigure.util.IpUtil;
import com.gewuyou.baseforge.entities.log.annotation.OperationLogging;
import com.gewuyou.baseforge.entities.log.entity.OperationLogDataEntity;
import com.gewuyou.baseforge.entities.log.entity.OperationLogProcessingObject;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * 操作日志处理程序
 *
 * @author gewuyou
 * @since 2024-11-21 15:29:59
 */
@Slf4j
public class OperationLogHandler implements IOperationLogHandler {
    protected final IOperationLoggingDataService operationLoggingDataService;
    protected final ObjectMapper objectMapper;

    public OperationLogHandler(
            IOperationLoggingDataService operationLoggingDataService,
            ObjectMapper objectMapper
    ) {
        this.objectMapper = objectMapper;
        this.operationLoggingDataService = operationLoggingDataService;
    }

    /**
     * 处理操作日志
     *
     * @param operationLogProcessingObject 操作日志处理对象
     */
    @Override
    public void handle(OperationLogProcessingObject operationLogProcessingObject) {
        JoinPoint joinPoint = operationLogProcessingObject.getJoinPoint();
        HttpServletRequest request = operationLogProcessingObject.getRequest();
        OperationLogging operationLogging = operationLogProcessingObject.getOperationLogging();
        String className = joinPoint.getTarget().getClass().getName();
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        String methodName = method.getName();
        String optMethod = className + "." + methodName;
        // 获取方法注解Operation
        Operation operation = method.getAnnotation(Operation.class);
        String ipAddress = IpUtil.getIpAddress(Objects.requireNonNull(request));
        // 是否记录请求参数
        String requestParams = null;
        if (operationLogging.logParams()) {
            // 获取方法的排除参数
            String[] excludeParams = operationLogging.exclude();
            // 获取参数名称
            Parameter[] parameters = method.getParameters();
            // 获取当前方法的参数
            Object[] args = joinPoint.getArgs();
            // 过滤参数
            Map<String, Object> filteredParams = new HashMap<>();
            for (int i = 0; i < parameters.length; i++) {
                // 获取参数名
                String paramName = parameters[i].getName();
                // 检查是否在排除列表中
                if (!Arrays.asList(excludeParams).contains(paramName)) {
                    filteredParams.put(paramName, args[i]);
                }

            }
            try {
                requestParams = objectMapper.writeValueAsString(filteredParams);
            } catch (JsonProcessingException e) {
                log.error("操作日志记录写入请求参数失败", e);
            }
            String resultStr = null;
            // 是否记录返回结果
            if (operationLogging.logResult()) {
                try {
                    resultStr = objectMapper.writeValueAsString(operationLogProcessingObject.getResult());
                } catch (JsonProcessingException e) {
                    log.error("操作日志记录写入返回结果失败", e);
                }
            }
            operationLoggingDataService
                    .processingOperationLog(
                            OperationLogDataEntity
                                    .builder()
                                    .optUrl(request.getRequestURI())
                                    .optType(operationLogging.type().getValue())
                                    .optMethod(optMethod)
                                    .optDesc(operation.description())
                                    .requestMethod(request.getMethod())
                                    .requestParams(requestParams)
                                    .result(resultStr)
                                    .ipAddress(ipAddress)
                                    .duration(operationLogProcessingObject.getDuration())
                                    .build());
        }
    }
}
