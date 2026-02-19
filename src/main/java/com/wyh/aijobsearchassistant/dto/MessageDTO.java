package com.wyh.aijobsearchassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 聊天消息 DTO
 * 用于返回单条聊天记录
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "聊天消息")
public class MessageDTO {

    @Schema(description = "消息ID")
    private Long id;

    @Schema(description = "会话ID")
    private String conversationId;

    @Schema(description = "消息角色（USER/ASSISTANT/SYSTEM）")
    private String role;

    @Schema(description = "消息内容")
    private String content;

    @Schema(description = "意图类型（仅AI消息有）")
    private String intent;

    @Schema(description = "技能标识（仅AI消息有）")
    private String skill;

    @Schema(description = "Token数量")
    private Integer tokenCount;

    @Schema(description = "发送时间")
    private LocalDateTime sendTime;

    @Schema(description = "使用的模型")
    private String model;
}
