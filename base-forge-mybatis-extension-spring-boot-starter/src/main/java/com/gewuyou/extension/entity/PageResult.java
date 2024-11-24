package com.gewuyou.extension.entity;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * 分页结果返回对象
 *
 * @author gewuyou
 * @since 2024-04-23 下午10:53:04
 */
@Schema(description = "分页结果返回对象")
@Data
@AllArgsConstructor
@Builder
public class PageResult<T> {
    /**
     * 记录
     */
    @Schema(description = "记录")
    private List<T> records;

    /**
     * 总记录数
     */
    @Schema(description = "总记录数")
    private Long count;

    public PageResult() {
        this.records = List.of();
        this.count = 0L;
    }

    public PageResult(Page<T> page) {
        this.records = page.getRecords();
        this.count = page.getTotal();
    }
}
