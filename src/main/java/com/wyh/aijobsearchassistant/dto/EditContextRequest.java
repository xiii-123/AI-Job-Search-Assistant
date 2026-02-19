package com.wyh.aijobsearchassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 编辑上下文请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "编辑上下文请求")
public class EditContextRequest {

    @Schema(description = "会话ID", example = "uuid-here", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "会话ID不能为空")
    private String conversationId;

    @Schema(description = "新的上下文内容", example = "你是一位专业的Java面试官，专注于后端开发相关问题", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "上下文内容不能为空")
    @Size(max = 2000, message = "上下文内容不能超过2000字符")
    private String newContext;
}
