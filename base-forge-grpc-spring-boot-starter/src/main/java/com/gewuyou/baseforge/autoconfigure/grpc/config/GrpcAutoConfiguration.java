package com.gewuyou.baseforge.autoconfigure.grpc.config;

import com.gewuyou.baseforge.autoconfigure.grpc.config.entity.GrpcProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * gRPC 自动配置
 *
 * @author gewuyou
 * @since 2024-12-05 14:44:51
 */
@Configuration
@EnableConfigurationProperties({GrpcProperties.class})
public class GrpcAutoConfiguration {
}
