package com.gewuyou.baseforge.entities.web.util;

import com.gewuyou.baseforge.entities.web.entity.PageResult;

import java.util.Objects;

/**
 * 分页工具
 *
 * @author gewuyou
 * @since 2024-04-24 下午10:00:53
 */
public class PageUtil {
    private static final ThreadLocal<PageResult<?>> PAGE_RESULT_THREAD_LOCAL = new ThreadLocal<>();

    private PageUtil() {
    }

    /**
     * 获取当前页
     * @return 当前页
     */
    public static PageResult<?> getCurrentPageResult() {
        PageResult<?> page = PAGE_RESULT_THREAD_LOCAL.get();
        if (Objects.isNull(page)) {
            setCurrentPageResult(PageResult.empty());
        }
        return PAGE_RESULT_THREAD_LOCAL.get();
    }

    /**
     * 设置当前页
     * @param page 当前页
     */
    public static void setCurrentPageResult(PageResult<?> page) {
        PAGE_RESULT_THREAD_LOCAL.set(page);
    }

    /**
     * 获取当前页码
     * @return 当前页码
     */
    public static long getCurrentPage() {
        return getCurrentPageResult().getCurrentPage();
    }

    /**
     * 获取分页尺寸
     * @return 分页尺寸
     */
    public static long getPageSize() {
        return getCurrentPageResult().getPageSize();
    }

    /**
     * 获取应当偏移的记录数
     * @return 应当偏移的记录数
     */
    public static Long getLimitCurrent() {
        return (getCurrentPage() - 1) * getPageSize();
    }

    /**
     * 移除当前页
     */
    public static void remove() {
        PAGE_RESULT_THREAD_LOCAL.remove();
    }
}
