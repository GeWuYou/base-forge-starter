package com.gewuyou.baseforge.entities.web.entity;

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
    private long totalRecords;
    /**
     * 总页数
     */
    @Schema(description = "总页数")
    private long totalPages;
    /**
     * 当前页码
     */
    @Schema(description = "当前页码")
    private long currentPage;

    /**
     * 每页大小
     */
    @Schema(description = "每页大小")
    private long pageSize;

    /**
     * 是否有上一页
     */
    @Schema(description = "是否有上一页")
    private boolean hasPrevious;

    /**
     * 是否有下一页
     */
    @Schema(description = "是否有下一页")
    private boolean hasNext;

    public PageResult() {
        this.records = List.of();
        this.totalRecords = 0L;
        this.currentPage = 1L;
        this.pageSize = 10L;
        this.totalPages = 0L;
        this.hasPrevious = false;
        this.hasNext = false;
    }

    public static <T> PageResult<T> of() {
        return new PageResult<>();
    }

    public static <T> PageResult<T> of(long currentPage,long pageSize, long totalRecords,List<T> records) {
        long totalPages = totalRecords % pageSize == 0? totalRecords / pageSize : totalRecords / pageSize + 1;
        return PageResult
                .<T>builder()
                .records(records)
                .totalRecords(totalRecords)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .hasPrevious(currentPage > 1)
                .hasNext(currentPage < totalPages)
                .build();
    }
    public static <T> PageResult<T> of(int currentPage,int pageSize, long totalRecords,List<T> records) {
        long totalPages = totalRecords % pageSize == 0? totalRecords / pageSize : totalRecords / pageSize + 1;
        return PageResult
                .<T>builder()
                .records(records)
                .totalRecords(totalRecords)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .hasPrevious(currentPage > 1)
                .hasNext(currentPage < totalPages)
                .build();
    }
    public static <T> PageResult<T> of(long currentPage,long pageSize) {
        return PageResult
                .<T>builder()
                .currentPage(currentPage)
                .pageSize(pageSize)
                .build();
    }
    public static <T> PageResult<T> empty() {
        return of();
    }
}
