package com.wyh.aijobsearchassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 会话 DTO
 * 用于返回会话信息给前端
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "会话信息")
public class ConversationDTO {

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "会话名称")
    private String name;

    @Schema(description = "消息总数")
    private Integer messageCount;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "最后更新时间")
    private LocalDateTime updatedAt;

    @Schema(description = "会话状态（ACTIVE/DELETED/ARCHIVED）")
    private String status;
}
