package com.gewuyou.baseforge.autoconfigure.mybatisextension.config;

import com.baomidou.mybatisplus.autoconfigure.ConfigurationCustomizer;
import com.gewuyou.baseforge.autoconfigure.mybatisextension.handler.MyBatisMetaObjectHandler;
import com.gewuyou.baseforge.autoconfigure.mybatisextension.interceptor.SqlInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis 自动配置
 *
 * @author gewuyou
 * @since 2024-11-13 12:22:48
 */
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
