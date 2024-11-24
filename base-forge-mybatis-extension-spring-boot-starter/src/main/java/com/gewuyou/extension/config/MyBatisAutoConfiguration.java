package com.gewuyou.extension.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.gewuyou.extension.handler.MyBatisMetaObjectHandler;
import com.gewuyou.extension.interceptor.SqlInterceptor;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 自动配置
 *
 * @author gewuyou
 * @since 2024-11-13 12:22:48
 */
@EnableAutoConfiguration
@Configuration
public class MyBatisAutoConfiguration {

    /**
     * 自定义 MybatisSqlInterceptor
     * @return MybatisSqlInterceptor
     */
    @Bean
    public ConfigurationCustomizer mybatisConfigurationCustomizer() {
        return configuration -> configuration.addInterceptor(new SqlInterceptor());
    }
    /**
     * 自定义 MybatisMetaObjectHandler
     * @return MybatisMetaObjectHandler
     */
    @Bean
    public MyBatisMetaObjectHandler mybatisMetaObjectHandler() {
        return new MyBatisMetaObjectHandler();
    }
}
