package com.wyh.aijobsearchassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 分页响应 DTO
 * 统一的分页响应格式
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页响应结果")
public class PageResponseDTO<T> {

    @Schema(description = "当前页码")
    private Integer pageNum;

    @Schema(description = "每页大小")
    private Integer pageSize;

    @Schema(description = "总记录数")
    private Long total;

    @Schema(description = "总页数")
    private Integer totalPages;

    @Schema(description = "当前页数据")
    private List<T> records;

    @Schema(description = "是否有下一页")
    private Boolean hasNext;

    @Schema(description = "是否有上一页")
    private Boolean hasPrevious;

    /**
     * 从 Spring Data Page 对象构建分页响应
     */
    public static <T> PageResponseDTO<T> of(org.springframework.data.domain.Page<T> page) {
        return PageResponseDTO.<T>builder()
                .pageNum(page.getNumber() + 1)
                .pageSize(page.getSize())
                .total(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .records(page.getContent())
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * 构建空分页响应
     */
    public static <T> PageResponseDTO<T> empty() {
        return PageResponseDTO.<T>builder()
                .pageNum(1)
                .pageSize(10)
                .total(0L)
                .totalPages(0)
                .records(List.of())
                .hasNext(false)
                .hasPrevious(false)
                .build();
    }
}
