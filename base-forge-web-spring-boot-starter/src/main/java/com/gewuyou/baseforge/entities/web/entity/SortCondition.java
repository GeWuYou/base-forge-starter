package com.gewuyou.baseforge.entities.web.entity;


import com.gewuyou.baseforge.entities.web.enums.SortDirection;
import lombok.*;

/**
 * 排序条件
 *
 * @author gewuyou
 * @since 2025-01-16 16:11:47
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SortCondition {
    /**
     * 排序字段
     */
    private String field;
    /**
     * 排序方向
     */
    private SortDirection direction;
}
