package com.gewuyou.baseforge.entities.log.entity;


import com.gewuyou.baseforge.entities.log.annotation.OperationLogging;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.aspectj.lang.JoinPoint;

/**
 * 操作日志处理对象
 *
 * @author gewuyou
 * @since 2024-11-21 15:22:39
 */
@Data
@Builder
public class OperationLogProcessingObject {
    /**
     * 操作日志注解信息
     */
    OperationLogging operationLogging;
    /**
     * 方法返回对象
     */
    Object result;
    /**
     * 执行耗时(单位毫秒)
     */
    long duration;
    /**
     * 切点
     */
    private JoinPoint joinPoint;
    /**
     * 请求对象
     */
    private HttpServletRequest request;
}
