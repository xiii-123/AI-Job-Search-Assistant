package com.wyh.aijobsearchassistant.controller;

import com.wyh.aijobsearchassistant.dto.ChangePasswordRequest;
import com.wyh.aijobsearchassistant.dto.UserProfileDTO;
import com.wyh.aijobsearchassistant.model.ApiResponse;
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
 * 用户信息管理 Controller
 * 提供用户信息查询、密码修改等接口
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "用户信息管理", description = "用户信息查询、密码修改、昵称头像修改等接口")
public class UserInfoController {

    private final UserInfoService userInfoService;

    @GetMapping("/profile")
    @Operation(summary = "获取用户信息", description = "查询当前登录用户的基本信息")
    public ApiResponse<UserProfileDTO> getUserProfile(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails) {

        Long userId = getUserIdFromUserDetails(userDetails);
        UserProfileDTO profile = userInfoService.getUserProfile(userId);

        return ApiResponse.success(profile);
    }

    @PostMapping("/change-password")
    @Operation(summary = "修改密码", description = "验证原密码后修改新密码")
    public ApiResponse<Map<String, Object>> changePassword(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Valid @RequestBody ChangePasswordRequest request) {

        Long userId = getUserIdFromUserDetails(userDetails);
        userInfoService.changePassword(userId, request);

        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "密码修改成功，请重新登录");

        return ApiResponse.success("密码修改成功", result);
    }

    @PutMapping("/nickname")
    @Operation(summary = "修改昵称", description = "修改用户的显示名称")
    public ApiResponse<Map<String, Object>> updateNickname(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "新昵称", required = true)
            @RequestParam String nickname) {

        Long userId = getUserIdFromUserDetails(userDetails);
        userInfoService.updateNickname(userId, nickname);

        return ApiResponse.success("昵称修改成功", Map.of("nickname", nickname));
    }

    @PutMapping("/avatar")
    @Operation(summary = "修改头像", description = "修改用户头像URL（预留功能）")
    public ApiResponse<Map<String, Object>> updateAvatar(
            @Parameter(description = "JWT Token", required = true)
            @AuthenticationPrincipal UserDetails userDetails,

            @Parameter(description = "头像URL", required = true)
            @RequestParam String avatarUrl) {

        Long userId = getUserIdFromUserDetails(userDetails);
        userInfoService.updateAvatar(userId, avatarUrl);

        return ApiResponse.success("头像修改成功", Map.of("avatarUrl", avatarUrl));
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
