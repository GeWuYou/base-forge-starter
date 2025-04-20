package com.gewuyou.baseforge.autoconfigure.web.config.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 分页配置
 *
 * @author gewuyou
 * @since 2024-12-18 16:00:48
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ConfigurationProperties(prefix = "base-forge.web.page")
public class PageProperties {
    /**
     * 默认每页显示数量
     */
    private long defaultPageSize = 10;
    /**
     * 最大每页显示数量
     */
    private long maxPageSize = 100;
    /**
     * 默认当前页
     */
    private long defaultCurrentPage = 1;
    /**
     * 分页页码参数名
     */
    private String currentPageParamName = "page";
    /**
     * 分页大小参数名
     */
    private String pageSizeParamName = "size";
    /**
     * 是否启用拦截器
     */
    private Boolean enable = false;
}
