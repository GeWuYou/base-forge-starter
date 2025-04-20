package com.gewuyou.baseforge.autoconfigure.web.interceptor;


import com.gewuyou.baseforge.autoconfigure.web.config.entity.PageProperties;
import com.gewuyou.baseforge.entities.web.entity.PageResult;
import com.gewuyou.baseforge.entities.web.util.PageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;


/**
 * 分页拦截器
 *
 * @author gewuyou
 * @since 2024-05-22 下午9:20:58
 */
@Slf4j
public class PaginationInterceptor implements HandlerInterceptor {

    private final PageProperties pageProperties;

    public PaginationInterceptor(PageProperties pageProperties) {
        this.pageProperties = pageProperties;
    }

    @Override
    public boolean preHandle(HttpServletRequest request
            , @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        long currentPage = Optional.ofNullable(request.getParameter(pageProperties.getCurrentPageParamName()))
                .map(value -> getValue(value, pageProperties.getDefaultCurrentPage()))
                .orElse(pageProperties.getDefaultCurrentPage());
        long pageSize = Optional.ofNullable(request.getParameter(pageProperties.getPageSizeParamName()))
                .map(value -> getValue(value, pageProperties.getDefaultPageSize()))
                .orElse(pageProperties.getDefaultPageSize());
        log.debug("当前页码:{}, 页大小:{}", currentPage, pageSize);
        // 设置分页信息
        PageUtil.setCurrentPageResult(PageResult.of(currentPage, pageSize));
        return true;
    }

    /**
     * 获取值
     * @param value 字符串值
     * @param defaultValue 默认值
     * @return long值
     */
    private long getValue(String value, long defaultValue) {
        try {
            long longValue = Long.parseLong(value);
            return longValue <= 0 ? defaultValue : longValue;
        } catch (NumberFormatException e) {
            log.warn("参数{}格式错误, 使用默认值:{}", value, defaultValue);
            return defaultValue;
        }
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        PageUtil.remove();
    }
}
