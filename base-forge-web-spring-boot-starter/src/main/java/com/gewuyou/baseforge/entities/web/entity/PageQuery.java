package com.gewuyou.baseforge.entities.web.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gewuyou.baseforge.entities.web.enums.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 分页查询条件实体类
 *
 * @author gewuyou
 * @since 2025-01-16 16:01:12
 */
@Schema(description = "分页查询条件实体类")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PageQuery<T> {
    /**
     * 当前页码(默认1)
     */
    @Schema(description = "当前页码(默认1)")
    private int page = 1;
    /**
     * 每页条数(默认10)
     */
    @Schema(description = "每页条数(默认10)")
    private int pageSize = 10;
    /**
     * 排序字段(单一字段)
     */
    @Schema(description = "排序字段(单一字段)")
    private String sortBy = "createdAt";
    /**
     * 排序方向(单一字段，ASC或DESC)
     */
    @Schema(description = "排序方向(单一字段，ASC或DESC)")
    private SortDirection sortDirection = SortDirection.DESC;
    /**
     * 排序条件实体类
     */
    @Schema(description = "排序条件实体类")
    private List<SortCondition> sortConditions = Collections.emptyList();
    /**
     * 关键字搜索，常用于模糊查询
     */
    @Schema(description = "关键字搜索，常用于模糊查询")
    private String keyword;
    /**
     * 自定义过滤条件实体类
     */
    @Schema(description = "自定义过滤条件实体类")
    private T filter;

    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    private LocalDateTime startDate;
    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    private LocalDateTime endDate;
    /**
     * 是否启用
     */
    @Schema(description = "是否启用")
    private Boolean isEnabled;
    /**
     * 是否删除
     */
    @Schema(description = "是否删除")
    private Boolean isDeleted = false;
}
