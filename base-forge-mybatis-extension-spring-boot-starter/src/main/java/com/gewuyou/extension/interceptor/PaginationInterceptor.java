package com.gewuyou.extension.interceptor;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.gewuyou.extension.util.PageUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Objects;
import java.util.Optional;



/**
 * 分页拦截器
 *
 * @author gewuyou
 * @since 2024-05-22 下午9:20:58
 */
@Slf4j
@Component
public class PaginationInterceptor implements HandlerInterceptor {

    //region 分页相关常量
    /**
     * 当前页码
     */
    public static final String CURRENT = "current";

    /**
     * 分页尺寸
     */
    public static final String SIZE = "size";

    /**
     * 默认每页大小
     */
    public static final String DEFAULT_SIZE = "10";
    //endregion
    @Override
    public boolean preHandle(HttpServletRequest request
            , @NonNull HttpServletResponse response,
                             @NonNull Object handler) {
        String currentPage = request.getParameter(CURRENT);
        String pageSize = Optional.ofNullable(request.getParameter(SIZE)).orElse(DEFAULT_SIZE);
        if (!Objects.isNull(currentPage) && !StringUtils.isEmpty(currentPage)) {
            PageUtil.setCurrentPage(new Page<>(Long.parseLong(currentPage), Long.parseLong(pageSize)));
        }
        return true;
    }

    @Override
    public void afterCompletion(@NonNull HttpServletRequest request, @NonNull HttpServletResponse response, @NonNull Object handler, Exception ex) {
        PageUtil.remove();
    }
}
