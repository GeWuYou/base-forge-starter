package com.gewuyou.baseforge.entities.web.entity;


import com.gewuyou.baseforge.entities.web.enums.SortDirection;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

/**
 * 排序条件
 *
 * @author gewuyou
 * @since 2025-01-16 16:11:47
 */
@Schema(description = "排序条件")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SortCondition {
    /**
     * 排序字段
     */
    @Schema(description = "排序字段")
    private String field;
    /**
     * 排序方向
     */
    @Schema(description = "排序方向")
    private SortDirection direction;
}
