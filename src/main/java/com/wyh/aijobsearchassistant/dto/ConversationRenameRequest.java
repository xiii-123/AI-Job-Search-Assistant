package com.wyh.aijobsearchassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 会话重命名请求 DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会话重命名请求")
public class ConversationRenameRequest {

    @Schema(description = "会话ID", example = "uuid-here", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "会话ID不能为空")
    private String conversationId;

    @Schema(description = "新会话名称", example = "Java面试准备", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotBlank(message = "会话名称不能为空")
    @Size(min = 1, max = 200, message = "会话名称长度必须在1-200之间")
    private String newName;
}
