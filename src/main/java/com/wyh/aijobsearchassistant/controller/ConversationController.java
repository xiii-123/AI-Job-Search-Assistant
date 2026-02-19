package com.wyh.aijobsearchassistant.controller;

import com.wyh.aijobsearchassistant.dto.ConversationDTO;
import com.wyh.aijobsearchassistant.dto.ConversationRenameRequest;
import com.wyh.aijobsearchassistant.dto.PageRequestDTO;
import com.wyh.aijobsearchassistant.dto.PageResponseDTO;
import com.wyh.aijobsearchassistant.model.ApiResponse;
import com.wyh.aijobsearchassistant.service.ConversationService;
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

import java.util.HashMap;
import java.util.Map;

/**
 * 会话管理 Controller
 * 提供会话的增删改查接口
 */
@RestController
@RequestMapping("/conversations")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "会话管理", description = "会话的增删改查、列表查询、重命名等接口")
public class ConversationController {

    private final ConversationService conversationService;
    private final UserInfoService userInfoService;

    @GetMapping
    @Operation(summary = "查询会话列表", description = "分页查询用户的所有会话，支持按名称关键词过滤")
    public ApiResponse<PageResponseDTO<ConversationDTO>> getConversations(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "会话名称关键词（模糊搜索）")
            @RequestParam(required = false) String keyword,

            @Valid @ModelAttribute PageRequestDTO pageRequestDTO) {

        Long userId = getUserIdFromUserDetails(userDetails);
        PageResponseDTO<ConversationDTO> result = conversationService.getConversations(
                userId, keyword, pageRequestDTO);

        return ApiResponse.success(result);
    }

    @PostMapping
    @Operation(summary = "创建新会话", description = "创建一个新的对话会话")
    public ApiResponse<ConversationDTO> createConversation(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "会话名称（可选，默认为新对话）")
            @RequestParam(required = false, defaultValue = "新对话") String name) {

        Long userId = getUserIdFromUserDetails(userDetails);
        ConversationDTO result = conversationService.createConversation(userId, name);

        return ApiResponse.success("会话创建成功", result);
    }

    @GetMapping("/{conversationId}")
    @Operation(summary = "获取会话详情", description = "根据会话ID查询会话详细信息")
    public ApiResponse<ConversationDTO> getConversation(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "会话ID", required = true)
            @PathVariable String conversationId) {

        Long userId = getUserIdFromUserDetails(userDetails);
        ConversationDTO result = conversationService.getConversation(userId, conversationId);

        return ApiResponse.success(result);
    }

    @PutMapping("/rename")
    @Operation(summary = "重命名会话", description = "修改指定会话的名称")
    public ApiResponse<ConversationDTO> renameConversation(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Valid @RequestBody ConversationRenameRequest request) {

        Long userId = getUserIdFromUserDetails(userDetails);
        ConversationDTO result = conversationService.renameConversation(userId, request);

        return ApiResponse.success("会话重命名成功", result);
    }

    @DeleteMapping("/{conversationId}")
    @Operation(summary = "删除会话", description = "删除指定会话及其所有聊天记录")
    public ApiResponse<Map<String, Object>> deleteConversation(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "会话ID", required = true)
            @PathVariable String conversationId) {

        Long userId = getUserIdFromUserDetails(userDetails);

        // 获取删除前的会话总数
        long beforeCount = conversationService.getUserConversationCount(userId);

        // 执行删除
        conversationService.deleteConversation(userId, conversationId);

        // 获取删除后的会话总数
        long afterCount = conversationService.getUserConversationCount(userId);

        Map<String, Object> result = new HashMap<>();
        result.put("deleted", true);
        result.put("conversationId", conversationId);
        result.put("remainingConversations", afterCount);

        return ApiResponse.success("会话删除成功", result);
    }

    @DeleteMapping("/{conversationId}/clear")
    @Operation(summary = "清空会话", description = "清空指定会话的所有聊天记录，保留会话主体")
    public ApiResponse<Map<String, Object>> clearConversation(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "会话ID", required = true)
            @PathVariable String conversationId) {

        Long userId = getUserIdFromUserDetails(userDetails);
        conversationService.clearConversation(userId, conversationId);

        Map<String, Object> result = new HashMap<>();
        result.put("cleared", true);
        result.put("conversationId", conversationId);

        return ApiResponse.success("会话清空成功", result);
    }

    /**
     * 从 UserDetails 中提取 userId
     */
    private Long getUserIdFromUserDetails(UserDetails userDetails) {
        // 假设 UserDetails 的 username 实际存储的是 userId
        // 如果不是，需要通过 UserService 查询
        try {
            return Long.parseLong(userDetails.getUsername());
        } catch (NumberFormatException e) {
            // 如果是用户名而非ID，通过UserService查询
            return userInfoService.getUserIdByUsername(userDetails.getUsername());
        }
    }
}
