package com.wyh.aijobsearchassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页请求 DTO
 * 统一的分页查询参数
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "分页请求参数")
public class PageRequestDTO {

    @Schema(description = "页码（从1开始）", example = "1", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "页码必须大于等于1")
    private Integer pageNum = 1;

    @Schema(description = "每页大小（默认10，最大100）", example = "10", requiredMode = Schema.RequiredMode.REQUIRED)
    @Min(value = 1, message = "每页大小必须大于等于1")
    @Max(value = 100, message = "每页大小不能超过100")
    private Integer pageSize = 10;

    @Schema(description = "排序字段（默认按创建时间倒序）", example = "createdAt")
    private String sortField = "createdAt";

    @Schema(description = "排序方向（ASC/DESC）", example = "DESC")
    private String sortDirection = "DESC";
}
