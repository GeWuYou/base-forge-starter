package com.gewuyou.baseforge.autoconfigure.async.config;

import com.gewuyou.baseforge.autoconfigure.async.aspect.AsyncMdcContextAspect;
import com.gewuyou.baseforge.autoconfigure.async.config.entity.AsyncTaskExecutorProperties;
import com.gewuyou.baseforge.autoconfigure.async.service.AsyncService;
import com.gewuyou.baseforge.autoconfigure.async.service.impl.AsyncServiceImpl;
import com.gewuyou.baseforge.core.constants.WebCommonConstant;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
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
@Configuration
@EnableConfigurationProperties(AsyncTaskExecutorProperties.class)
public class AsyncTaskExecutorAutoConfiguration implements AsyncConfigurer, DisposableBean {
    private final AsyncTaskExecutorProperties properties;
    private final RejectedExecutionHandler rejectedExecutionHandler;
    private ThreadPoolTaskExecutor executor;

    @Autowired
    public AsyncTaskExecutorAutoConfiguration(
            AsyncTaskExecutorProperties properties,
            @Lazy
            RejectedExecutionHandler rejectedExecutionHandler) {
        this.properties = properties;
        this.rejectedExecutionHandler = rejectedExecutionHandler;
    }

    /**
     * 创建异步MDC上下文切面 只有当配置中开启了async.task-executor.enableAsyncMdcAspect=true时才会创建
     * @return 异步MDC上下文切面
     */
    @Bean
    @ConditionalOnProperty(name = "async.task-executor.enableAsyncMdcAspect", havingValue = "true")
    public AsyncMdcContextAspect createAsyncMdcContextAspect() {
        return new AsyncMdcContextAspect();
    }

    @Bean
    @ConditionalOnMissingBean(RejectedExecutionHandler.class)
    public RejectedExecutionHandler defaultRejectedExecutionHandler() {
        return new ThreadPoolExecutor.CallerRunsPolicy();
    }

    @PostConstruct
    public void init() {
        checkProperties();
    }

    /**
     * The {@link Executor} instance to be used when processing async
     * method invocations.
     */
    @Override
    @Bean(name = "asyncTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
        // 设置核心线程数
        threadPoolTaskExecutor.setCorePoolSize(properties.getCorePoolSize());
        // 设置最大线程数
        threadPoolTaskExecutor.setMaxPoolSize(properties.getMaxPoolSize());
        // 设置队列容量
        threadPoolTaskExecutor.setQueueCapacity(properties.getQueueCapacity());
        // 设置线程活跃时间（秒）
        threadPoolTaskExecutor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        // 设置默认线程名称前缀
        threadPoolTaskExecutor.setThreadNamePrefix(properties.getThreadNamePrefix());
        // 设置拒绝策略    当前策略:CallerRunsPolicy  由调用者线程来运行任务使得执行退化为同步
        threadPoolTaskExecutor.setRejectedExecutionHandler(rejectedExecutionHandler);
        // 设置线程装饰器 设置MDC上下文
        threadPoolTaskExecutor.setTaskDecorator(runnable -> {
            Optional.ofNullable(MDC.getCopyOfContextMap()).ifPresent(
                    contextMap -> {
                        try {
                            MDC.setContextMap(contextMap);
                            runnable.run();
                        } finally {
                            MDC.clear();
                        }
                    }
            );
            return runnable;
        });
        threadPoolTaskExecutor.initialize();
        this.executor = threadPoolTaskExecutor;
        return threadPoolTaskExecutor;
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
        String requestId = MDC.get(WebCommonConstant.REQUEST_ID_MDC_KEY);
        if (Objects.isNull(requestId)) {
            return (ex, method, params) -> log.error("执行异步方法时发生异常，方法名： [{}]，参数：[{}]，异常信息： [{}]",
                    method.getName(), Arrays.toString(params), ex.getMessage(), ex);
        } else {
            return (ex, method, params) -> log.error("执行异步方法时发生异常，请求id： [{}]，方法名： [{}]，参数：[{}]，异常信息： [{}]",
                    requestId, method.getName(), Arrays.toString(params), ex.getMessage(), ex);
        }
    }

    /**
     * 创建默认的异步服务
     *
     * @return 异步服务
     */
    @Bean(name = "defaultAsyncService")
    @ConditionalOnMissingBean(AsyncService.class)
    public AsyncService createDefaultAsyncService() {
        return new AsyncServiceImpl(executor);
    }

    /**
     * Invoked by the containing {@code BeanFactory} on destruction of a bean.
     */
    @Override
    public void destroy() {
        if (Objects.nonNull(executor)) {
            executor.shutdown();
        }
    }
}
