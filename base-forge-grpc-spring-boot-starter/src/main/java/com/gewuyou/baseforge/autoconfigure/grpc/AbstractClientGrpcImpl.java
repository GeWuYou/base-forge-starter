package com.gewuyou.baseforge.autoconfigure.grpc;


import com.gewuyou.baseforge.autoconfigure.grpc.config.entity.GrpcConfiguration;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import io.grpc.stub.AbstractBlockingStub;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;

/**
 * 抽象客户端 gRPC 实现
 *
 * @author gewuyou
 * @since 2024-11-10 12:31:02
 */
public abstract class AbstractClientGrpcImpl<T extends AbstractBlockingStub<T>> implements ClientGrpc {
    private final GrpcConfiguration grpcConfiguration;
    /**
     * grpc 客户端管道
     */
    protected ManagedChannel managedChannel;

    protected T blockingStub;

    protected AbstractClientGrpcImpl(GrpcConfiguration grpcConfiguration) {
        this.grpcConfiguration = grpcConfiguration;
    }

    /**
     * 初始化阻塞存根
     * @param managedChannel 管道
     * @return 阻塞存根
     */
    protected abstract T initBlockingStub(ManagedChannel managedChannel);

    /**
     * 初始化
     */
    @Override
    @PostConstruct
    public void init() {
        managedChannel = ManagedChannelBuilder.forAddress(grpcConfiguration.getHost(), grpcConfiguration.getPort()).usePlaintext().build();
        blockingStub = initBlockingStub(managedChannel);
    }

    /**
     * 关闭管道
     */
    @Override
    @PreDestroy
    public void shutdown() {
        managedChannel.shutdown();
    }
}
