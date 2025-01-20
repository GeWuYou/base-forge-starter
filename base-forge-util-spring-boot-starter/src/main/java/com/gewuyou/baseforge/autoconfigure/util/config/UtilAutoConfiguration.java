package com.gewuyou.baseforge.autoconfigure.util.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gewuyou.baseforge.autoconfigure.util.BeanCopyUtil;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 工具 自动配置
 *
 * @author gewuyou
 * @since 2024-11-13 14:48:32
 */
@Configuration

public class UtilAutoConfiguration {

    @Bean
    public BeanCopyUtil beanCopyUtil(ObjectMapper objectMapper) {
        return new BeanCopyUtil(objectMapper);
    }
}
