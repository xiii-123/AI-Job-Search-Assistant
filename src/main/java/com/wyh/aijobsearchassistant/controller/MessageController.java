package com.wyh.aijobsearchassistant.controller;

import com.wyh.aijobsearchassistant.dto.EditContextRequest;
import com.wyh.aijobsearchassistant.dto.MessageDTO;
import com.wyh.aijobsearchassistant.dto.PageRequestDTO;
import com.wyh.aijobsearchassistant.dto.PageResponseDTO;
import com.wyh.aijobsearchassistant.model.ApiResponse;
import com.wyh.aijobsearchassistant.service.MessageService;
import com.wyh.aijobsearchassistant.service.UserInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 历史对话管理 Controller
 * 提供消息查询、删除、上下文编辑接口
 */
@RestController
@RequestMapping("/messages")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "历史对话管理", description = "历史消息查询、删除、上下文编辑等接口")
public class MessageController {

    private final MessageService messageService;
    private final UserInfoService userInfoService;

    @GetMapping("/conversation/{conversationId}")
    @Operation(summary = "查询会话历史消息", description = "分页查询指定会话的所有聊天记录，按时间倒序")
    public ApiResponse<PageResponseDTO<MessageDTO>> getConversationMessages(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "会话ID", required = true)
            @PathVariable String conversationId,

            @Valid @ModelAttribute PageRequestDTO pageRequestDTO) {

        Long userId = getUserIdFromUserDetails(userDetails);
        PageResponseDTO<MessageDTO> result = messageService.getConversationMessages(
                userId, conversationId, pageRequestDTO);

        return ApiResponse.success(result);
    }

    @DeleteMapping("/{messageId}")
    @Operation(summary = "删除单条消息", description = "删除指定的聊天记录")
    public ApiResponse<Map<String, Object>> deleteMessage(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "消息ID", required = true)
            @PathVariable Long messageId) {

        Long userId = getUserIdFromUserDetails(userDetails);
        messageService.deleteMessage(userId, messageId);

        return ApiResponse.success("消息删除成功", Map.of("deletedMessageId", messageId));
    }

    @PutMapping("/context")
    @Operation(summary = "编辑会话上下文", description = "手动修改会话的上下文内容，修改后影响后续对话")
    public ApiResponse<Map<String, Object>> editContext(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Valid @RequestBody EditContextRequest request) {

        Long userId = getUserIdFromUserDetails(userDetails);
        messageService.editConversationContext(userId, request);

        return ApiResponse.success("上下文修改成功", Map.of(
                "conversationId", request.getConversationId(),
                "contextUpdated", true
        ));
    }

    @GetMapping("/context/{conversationId}/summary")
    @Operation(summary = "获取会话上下文摘要", description = "获取会话的统计信息和上下文状态")
    public ApiResponse<Map<String, Object>> getContextSummary(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "会话ID", required = true)
            @PathVariable String conversationId) {

        Long userId = getUserIdFromUserDetails(userDetails);
        Map<String, Object> summary = messageService.getContextSummary(userId, conversationId);

        return ApiResponse.success(summary);
    }

    /**
     * 从 UserDetails 中提取 userId
     */
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        try {
            return Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            // 如果是用户名而非ID，通过UserService查询
            return userInfoService.getUserIdByUsername(userDetails.getUsername());
        }
    }
}
