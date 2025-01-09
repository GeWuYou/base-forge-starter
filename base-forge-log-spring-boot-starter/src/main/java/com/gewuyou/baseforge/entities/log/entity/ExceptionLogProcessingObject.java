package com.gewuyou.baseforge.entities.log.entity;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Builder;
import lombok.Data;
import org.aspectj.lang.JoinPoint;

/**
 * 异常日志处理对象
 *
 * @author gewuyou
 * @since 2024-11-15 15:57:01
 */
@Data
@Builder
public class ExceptionLogProcessingObject {
    /**
     * 切点
     */
    private JoinPoint joinPoint;
    /**
     * 请求对象
     */
    private HttpServletRequest request;
    /**
     * 异常对象
     */
    private Exception exception;
}
