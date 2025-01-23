package com.gewuyou.baseforge.autoconfigure.async.service.impl;

import com.gewuyou.baseforge.autoconfigure.async.service.AsyncService;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 异步服务实现类
 *
 * @author gewuyou
 * @since 2024-12-18 22:51:25
 */
public record AsyncServiceImpl(Executor executor) implements AsyncService {

    @Override
    public Executor getAsyncExecutor() {
        return this.executor;
    }

    /**
     * 异步执行Supplier
     *
     * @param supplier 任务提供者
     * @param <T>      返回值类型
     * @return CompletableFuture
     */
    @Override
    public <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
        return CompletableFuture.supplyAsync(supplier, executor);
    }

    /**
     * 异步执行Runnable
     *
     * @param runnable 任务
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<Void> runAsync(Runnable runnable) {
        return CompletableFuture.runAsync(runnable, executor);
    }

    /**
     * 批量异步执行Runnable
     *
     * @param runnableList 任务列表
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<Void> batchRunAsync(List<Runnable> runnableList) {
        return CompletableFuture.allOf(runnableList
                .stream()
                .map(this::runAsync)
                .toList()
                .toArray(new CompletableFuture[0]));
    }

    /**
     * 批量异步执行Runnable
     *
     * @param runnableList 任务列表
     * @param callback     回调函数
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<Void> batchRunAsync(List<Runnable> runnableList, Runnable callback) {
        return batchRunAsync(runnableList).thenRun(callback);
    }

    /**
     * 异步执行任务并处理异常
     *
     * @param runnable     任务
     * @param errorHandler 异常处理函数
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<Void> runAsyncWithExceptionHandler(Runnable runnable, Function<Throwable, Void> errorHandler) {
        return runAsync(runnable).exceptionally(errorHandler);
    }

    /**
     * 异步执行任务，并在任务完成后执行回调
     *
     * @param runnable 任务
     * @param callback 任务完成后的回调
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<Void> runAsyncWithCallback(Runnable runnable, Runnable callback) {
        return runAsync(runnable).thenRun(callback);
    }

    /**
     * 异步执行任务，支持超时控制
     *
     * @param runnable 任务
     * @param timeout  超时阈值（单位：毫秒）
     * @return CompletableFuture
     */
    @Override
    public CompletableFuture<Void> runAsyncWithTimeout(Runnable runnable, long timeout) {
        return runAsync(runnable).orTimeout(timeout, TimeUnit.MILLISECONDS);
    }

    /**
     * 异步执行任务，支持超时控制
     *
     * @param supplier 任务提供者
     * @param timeout  超时阈值（单位：毫秒）
     * @return CompletableFuture
     */
    @Override
    public <T> CompletableFuture<T> supplyAsyncWithTimeout(Supplier<T> supplier, long timeout) {
        return supplyAsync(supplier).orTimeout(timeout, TimeUnit.MILLISECONDS);
    }


    /**
     * 异步执行任务，支持超时控制，并返回结果
     *
     * @param supplier 任务提供者
     * @param timeout  超时阈值（单位：毫秒）
     * @param callback 任务完成后的回调
     * @return CompletableFuture
     */
    @Override
    public <T> CompletableFuture<Void> supplyAsyncWithTimeout(Supplier<T> supplier, long timeout, Consumer<T> callback) {
        return supplyAsyncWithTimeout(supplier, timeout).thenAccept(callback);
    }

    /**
     * 批量异步执行任务，并收集所有任务的结果
     *
     * @param suppliers 任务提供者列表
     * @return CompletableFuture<List < T>> 返回所有任务的结果
     */
    @Override
    public <T> CompletableFuture<List<T>> batchSupplyAsync(List<Supplier<T>> suppliers) {
        // 将每个任务异步执行，并返回对应的 CompletableFuture
        List<CompletableFuture<T>> futures = suppliers.stream()
                .map(this::supplyAsync)
                .toList();
        // 使用 CompletableFuture.allOf 等待所有任务完成，并收集结果
        return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]))
                .thenApply(v ->
                        futures.stream()
                                // 获取每个任务的结果
                                .map(CompletableFuture::join)
                                // 收集到 List 中
                                .toList());
    }
}
