package com.gewuyou.baseforge.entities.web.entity;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 分页请求
 *
 * @author gewuyou
 * @since 2024-12-17 23:45:09
 */
@Schema(description = "基本分页请求")
@AllArgsConstructor
@NoArgsConstructor
@Data
@ToString
public class PageRequest {
    /**
     * 排序字段 格式：field:asc/desc,field2:asc/desc
     */
    @Schema(description = "排序字段", example = "field:asc,field2:desc")
    private String sort;
    /**
     * 关键字
     */
    @Schema(description = "关键字")
    private String keyword;
    /**
     * 开始日期
     */
    @Schema(description = "开始日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime startDate;

    /**
     * 结束日期
     */
    @Schema(description = "结束日期")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime endDate;
}
