package com.gewuyou.baseforge.autoconfigure.async.config.entity;

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
     * CPU核数
     */
    private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

    /**
     * 核心线程数(最少2，最多CPU核数-1，最多4个核心线程)
     */
    private static final int CORE_POOL_SIZE =  Math.clamp(CPU_COUNT - 1L, 2, 4);

    /**
     * 最大线程数 (CPU核数*2+1)
     */
    private static final int MAX_POOL_SIZE = CPU_COUNT * 2 + 1;
    /**
     * 队列容量
     */
    private static final int WORK_QUEUE = 20;
    /**
     * 线程存活时间
     */
    private static final int KEEP_ALIVE_TIME = 30;
    /**
     * 核心线程数
     */
    private int corePoolSize = CORE_POOL_SIZE;
    /**
     * 最大线程数
     */
    private int maxPoolSize = MAX_POOL_SIZE;
    /**
     * 队列容量
     */
    private int queueCapacity = WORK_QUEUE;
    /**
     * 线程存活时间
     */
    private int keepAliveSeconds = KEEP_ALIVE_TIME;
    /**
     * 线程名称前缀
     */
    private String threadNamePrefix = "async-task-executor-";
    /**
     * 是否开启异步MDC切面
     */
    private boolean enableAsyncMdcAspect = false;
}
