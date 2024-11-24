package com.gewuyou.async.config.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 异步配置
 *
 * @author gewuyou
 * @since 2024-11-13 16:52:35
 */
@ConfigurationProperties(prefix = "async.task-executor")
@Data
public class AsyncTaskExecutorProperties {
    /**
     * 核心线程数
     */
    private int corePoolSize =10;
    /**
     * 最大线程数
     */
    private int maxPoolSize = 20;
    /**
     * 队列容量
     */
    private int queueCapacity = 20;
    /**
     * 线程存活时间
     */
    private int keepAliveSeconds = 60;
    /**
     * 线程名称前缀
     */
    private String threadNamePrefix = "async-task-executor-";
}
