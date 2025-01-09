package com.gewuyou.baseforge.entities.log.entity;

import lombok.*;

/**
 * 异常日志数据实体
 *
 * @author gewuyou
 * @since 2024-11-17 18:54:05
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExceptionLogDataEntity {
    /**
     * 操作路径
     */
    private String optUrl;
    /**
     * 操作方法
     */
    private String optMethod;
    /**
     * 操作描述
     */
    private String optDesc;
    /**
     * 请求方法
     */
    private String requestMethod;
    /**
     * 请求参数
     */
    private String requestParams;
    /**
     * ip地址
     */
    private String ipAddress;
    /**
     * 异常名称
     */
    private String exceptionName;
    /**
     * 异常信息
     */
    private String exceptionInfo;
}
