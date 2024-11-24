package com.gewuyou.extension.config;

import com.gewuyou.extension.interceptor.PaginationInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * MyBatis 扩展自动配置
 *
 * @author gewuyou
 * @since 2024-11-13 10:23:32
 */
@EnableAutoConfiguration
@Configuration
public class MyBatisExtensionAutoConfiguration implements WebMvcConfigurer {
    private final PaginationInterceptor paginationInterceptor;

    @Autowired
    public MyBatisExtensionAutoConfiguration(@Lazy PaginationInterceptor paginationInterceptor) {
        this.paginationInterceptor = paginationInterceptor;
    }

    /**
     * 分页拦截器
     * @return 分页拦截器
     */
    @Bean
    public PaginationInterceptor paginationInterceptor() {
        return new PaginationInterceptor();
    }
    /**
     * 添加分页拦截器
     * @param registry 注册器
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        registry.addInterceptor(paginationInterceptor);
    }
}
