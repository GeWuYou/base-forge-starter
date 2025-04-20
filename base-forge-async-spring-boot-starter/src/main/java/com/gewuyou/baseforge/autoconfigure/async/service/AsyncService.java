package com.gewuyou.baseforge.autoconfigure.async.service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * 异步服务接口
 *
 * @author gewuyou
 * @since 2024-12-18 22:47:56
 */
public interface AsyncService {
    /**
     * 获取异步执行器
     * @return 异步执行器
     */
    Executor getAsyncExecutor();
    /**
     * 异步执行Supplier
     *
     * @param supplier 任务提供者
     * @param <T>      返回值类型
     * @return CompletableFuture
     */
    <T> CompletableFuture<T> supplyAsync(Supplier<T> supplier);

    /**
     * 异步执行Runnable
     *
     * @param runnable 任务
     * @return CompletableFuture
     */
    CompletableFuture<Void> runAsync(Runnable runnable);

    /**
     * 批量异步执行Runnable
     *
     * @param runnableList 任务列表
     * @return CompletableFuture
     */
    CompletableFuture<Void> batchRunAsync(List<Runnable> runnableList);

    /**
     * 批量异步执行Runnable
     *
     * @param runnableList 任务列表
     * @param callback     回调函数
     * @return CompletableFuture
     */
    CompletableFuture<Void> batchRunAsync(List<Runnable> runnableList, Runnable callback);

    /**
     * 异步执行任务并处理异常
     *
     * @param runnable     任务
     * @param errorHandler 异常处理函数
     * @return CompletableFuture
     */
    CompletableFuture<Void> runAsyncWithExceptionHandler(Runnable runnable, Function<Throwable, Void> errorHandler);

    /**
     * 异步执行任务，并在任务完成后执行回调
     *
     * @param runnable 任务
     * @param callback 任务完成后的回调
     * @return CompletableFuture
     */
    CompletableFuture<Void> runAsyncWithCallback(Runnable runnable, Runnable callback);

    /**
     * 异步执行任务，支持超时控制
     *
     * @param runnable 任务
     * @param timeout  超时阈值（单位：毫秒）
     * @return CompletableFuture
     */
    CompletableFuture<Void> runAsyncWithTimeout(Runnable runnable, long timeout);

    /**
     * 异步执行任务，支持超时控制
     *
     * @param supplier 任务提供者
     * @param timeout  超时阈值（单位：毫秒）
     * @return CompletableFuture
     */
    <T> CompletableFuture<T> supplyAsyncWithTimeout(Supplier<T> supplier, long timeout);

    /**
     * 异步执行任务，支持超时控制，并返回结果
     *
     * @param supplier 任务提供者
     * @param timeout  超时阈值（单位：毫秒）
     * @param callback 任务完成后的回调
     * @param <T>      返回类型
     * @return CompletableFuture
     */
    <T> CompletableFuture<Void> supplyAsyncWithTimeout(Supplier<T> supplier, long timeout, Consumer<T> callback);

    /**
     * 批量异步执行任务，并收集所有任务的结果
     *
     * @param supplierList 任务提供者列表
     * @param <T>          任务的返回类型
     * @return CompletableFuture<List < T>> 返回所有任务的结果
     */
    <T> CompletableFuture<List<T>> batchSupplyAsync(List<Supplier<T>> supplierList);

}
