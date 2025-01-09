package com.gewuyou.baseforge.autoconfigure.grpc.config.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * gRPC 配置
 *
 * @author gewuyou
 * @since 2024-12-06 09:57:35
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GrpcConfiguration {
    /**
     * gRPC 服务端地址
     */
    private String host;
    /**
     * gRPC 服务端端口
     */
    private int port;
}
