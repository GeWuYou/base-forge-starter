package com.gewuyou.baseforge.autoconfigure.grpc.config.entity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.Map;

/**
 * gRPC 属性
 *
 * @author gewuyou
 * @since 2024-12-05 14:46:53
 */
@Data
@ConfigurationProperties(prefix = "grpc")
public class GrpcProperties {
    /**
     * gRPC 服务配置映射
     */
    @NestedConfigurationProperty
    private Map<String,GrpcConfiguration> configuration = Map.of();
}
