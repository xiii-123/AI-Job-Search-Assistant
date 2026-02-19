package com.wyh.aijobsearchassistant.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 用户信息 DTO
 * 用于返回用户基本信息（隐藏敏感字段）
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "用户信息")
public class UserProfileDTO {

    @Schema(description = "用户ID")
    private Long userId;

    @Schema(description = "用户名")
    private String username;

    @Schema(description = "邮箱")
    private String email;

    @Schema(description = "显示名称")
    private String displayName;

    @Schema(description = "角色")
    private String role;

    @Schema(description = "账号状态（0-禁用，1-启用）")
    private Integer status;

    @Schema(description = "会话总数")
    private Long totalConversations;

    @Schema(description = "创建时间")
    private LocalDateTime createdAt;

    @Schema(description = "最后登录时间")
    private LocalDateTime lastLoginAt;
}
