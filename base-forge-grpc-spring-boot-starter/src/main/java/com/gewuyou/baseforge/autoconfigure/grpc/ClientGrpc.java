package com.gewuyou.baseforge.autoconfigure.grpc;

/**
 * 客户端 gRPC
 *
 * @author gewuyou
 * @since 2024-10-03 22:58:21
 */
public interface ClientGrpc {
    /**
     * 初始化
     */
    void init();

    /**
     * 关闭管道
     */
    void shutdown();
}
