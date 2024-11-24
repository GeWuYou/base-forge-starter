package com.gewuyou.async.config;

import com.gewuyou.async.config.entity.AsyncTaskExecutorProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * 异步自动配置
 *
 * @author gewuyou
 * @since 2024-11-13 16:49:47
 */
@Slf4j
@EnableAutoConfiguration
@Configuration
@EnableConfigurationProperties(AsyncTaskExecutorProperties.class)
public class AsyncTaskExecutorAutoConfiguration implements AsyncConfigurer , DisposableBean {
    private final AsyncTaskExecutorProperties properties;
    private final RejectedExecutionHandler rejectedExecutionHandler;
    private ThreadPoolTaskExecutor executor;
    @Autowired
    public AsyncTaskExecutorAutoConfiguration(
            AsyncTaskExecutorProperties properties,
            RejectedExecutionHandler rejectedExecutionHandler) {
        this.properties = properties;
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    @Bean
    @ConditionalOnMissingBean
    public RejectedExecutionHandler defaultRejectedExecutionHandler() {
        return new ThreadPoolExecutor.AbortPolicy();
    }

    /**
     * The {@link Executor} instance to be used when processing async
     * method invocations.
     */
    @Override
    @Bean(name = "asyncTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        checkProperties();
        // 设置核心线程数
        executor.setCorePoolSize(properties.getCorePoolSize());
        // 设置最大线程数
        executor.setMaxPoolSize(properties.getMaxPoolSize());
        // 设置队列容量
        executor.setQueueCapacity(properties.getQueueCapacity());
        // 设置线程活跃时间（秒）
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        // 设置默认线程名称前缀
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());
        // 设置拒绝策略    当前策略:AbortPolicy 超出执行队列会被舍弃并抛出异常
        executor.setRejectedExecutionHandler(rejectedExecutionHandler);
        executor.initialize();
        this.executor = executor;
        return executor;
    }

    /**
     * 检查配置属性
     */
    private void checkProperties() {
        if (properties.getCorePoolSize() <= 0) {
            throw new IllegalArgumentException("corePoolSize must be greater than 0");
        }
        if (properties.getMaxPoolSize() <= 0) {
            throw new IllegalArgumentException("maxPoolSize must be greater than 0");
        }
        if (properties.getCorePoolSize() > properties.getMaxPoolSize()) {
            throw new IllegalArgumentException("corePoolSize must be less than maxPoolSize");
        }
        if (properties.getQueueCapacity() <= 0) {
            throw new IllegalArgumentException("queueCapacity must be greater than 0");
        }
        if (properties.getKeepAliveSeconds() <= 0) {
            throw new IllegalArgumentException("keepAliveSeconds must be greater than 0");
        }
        if (!StringUtils.hasText(properties.getThreadNamePrefix())) {
            throw new IllegalArgumentException("threadNamePrefix must be greater than 0");
        }
    }

    /**
     * The {@link AsyncUncaughtExceptionHandler} instance to be used
     * when an exception is thrown during an asynchronous method execution
     * with {@code void} return type.
     */
    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> log.error("执行异步方法时发生异常方法名： [{}]，异常信息： [{}]", method.getName(), ex.getMessage(), ex);
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     *
     */
    @Override
    public void destroy() {
        if(Objects.nonNull(executor)){
            executor.shutdown();
        }
    }
}
