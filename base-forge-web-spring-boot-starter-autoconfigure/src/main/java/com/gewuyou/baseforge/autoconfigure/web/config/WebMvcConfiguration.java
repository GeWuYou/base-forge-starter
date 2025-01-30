package com.gewuyou.baseforge.autoconfigure.web.config;

import com.gewuyou.baseforge.autoconfigure.web.interceptor.AccessLimitInterceptor;
import com.gewuyou.baseforge.autoconfigure.web.interceptor.PaginationInterceptor;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Objects;

/**
 * Web MVC 配置
 *
 * @author gewuyou
 * @since 2024-11-13 11:01:23
 */
@Configuration
@NoArgsConstructor
@Slf4j
public class WebMvcConfiguration implements WebMvcConfigurer {

    private AccessLimitInterceptor accessLimitInterceptor;
    private PaginationInterceptor paginationInterceptor;

    @Autowired(required = false)
    public WebMvcConfiguration(
            @Nullable AccessLimitInterceptor accessLimitInterceptor,
            @Nullable PaginationInterceptor paginationInterceptor
    ) {
        this.accessLimitInterceptor = accessLimitInterceptor;
        this.paginationInterceptor = paginationInterceptor;
    }

    /**
     * Add Spring MVC lifecycle interceptors for pre- and post-processing of
     * controller method invocations and resource handler requests.
     * Interceptors can be registered to apply to all requests or be limited
     * to a subset of URL patterns.
     *
     * @param registry the InterceptorRegistry to add interceptors to
     */
    @Override
    public void addInterceptors(@NonNull InterceptorRegistry registry) {
        log.info("开始注册拦截器");
        if(Objects.nonNull(accessLimitInterceptor)) {
            log.info("添加访问限制拦截器");
            registry.addInterceptor(accessLimitInterceptor);
        }
        if(Objects.nonNull(paginationInterceptor)) {
            log.info("添加分页拦截器");
            registry.addInterceptor(paginationInterceptor);
        }
        log.info("注册拦截器完成");
    }
}
