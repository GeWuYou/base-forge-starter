package com.gewuyou.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.util.BeanCopyUtil;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具 自动配置
 *
 * @author gewuyou
 * @since 2024-11-13 14:48:32
 */
@EnableAutoConfiguration
@Configuration

public class UtilAutoConfiguration {

    @Bean
    public BeanCopyUtil beanCopyUtil(ObjectMapper objectMapper) {
        return new BeanCopyUtil(objectMapper);
    }
}
