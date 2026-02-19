package com.wyh.aijobsearchassistant.service;

import com.wyh.aijobsearchassistant.dto.ChangePasswordRequest;
import com.wyh.aijobsearchassistant.dto.UserProfileDTO;
import com.wyh.aijobsearchassistant.entity.SysUser;
import com.wyh.aijobsearchassistant.exception.BusinessException;
import com.wyh.aijobsearchassistant.exception.UserException;
import com.wyh.aijobsearchassistant.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户信息管理服务
 * 提供用户信息查询、密码修改等功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserInfoService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConversationService conversationService;

    /**
     * 获取用户基本信息
     */
    public UserProfileDTO getUserProfile(Long userId) {
        log.info("[UserInfoService] 获取用户信息: userId={}", userId);

        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 获取用户的会话总数
        Long totalConversations = conversationService.getUserConversationCount(userId);

        return UserProfileDTO.builder()
                .userId(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .displayName(user.getDisplayName())
                .role(user.getRole())
                .status(user.getStatus())
                .totalConversations(totalConversations)
                .createdAt(user.getCreatedAt())
                .lastLoginAt(user.getLastLoginAt())
                .build();
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long userId, ChangePasswordRequest request) {
        log.info("[UserInfoService] 修改密码: userId={}", userId);

        // 查询用户
        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        // 验证原密码
        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BusinessException(400, "原密码错误");
        }

        // 验证新密码和确认密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(400, "新密码与确认密码不一致");
        }

        // 检查新密码是否与原密码相同
        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new BusinessException(400, "新密码不能与原密码相同");
        }

        // 加密新密码
        String encodedNewPassword = passwordEncoder.encode(request.getNewPassword());
        user.setPassword(encodedNewPassword);

        // 更新用户
        userRepository.save(user);

        log.info("[UserInfoService] 密码修改成功: userId={}", userId);
    }

    /**
     * 修改用户昵称（预留扩展）
     */
    @Transactional
    public void updateNickname(Long userId, String nickname) {
        log.info("[UserInfoService] 修改昵称: userId={}, nickname={}", userId, nickname);

        SysUser user = userRepository.findById(userId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));

        user.setDisplayName(nickname);
        userRepository.save(user);

        log.info("[UserInfoService] 昵称修改成功: userId={}", userId);
    }

    /**
     * 修改用户头像（预留扩展）
     * TODO: 对接文件存储服务（如阿里云OSS）
     */
    @Transactional
    public void updateAvatar(Long userId, String avatarUrl) {
        log.info("[UserInfoService] 修改头像: userId={}, avatarUrl={}", userId, avatarUrl);

        // 暂时返回提示
        throw new BusinessException(501, "头像上传功能待实现，请等待后续版本");
    }

    /**
     * 更新最后登录时间
     */
    @Transactional
    public void updateLastLoginTime(Long userId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginAt(LocalDateTime.now());
            userRepository.save(user);
        });
    }

    /**
     * 根据用户名获取用户ID
     */
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(SysUser::getId)
                .orElseThrow(() -> new BusinessException(404, "用户不存在"));
    }
}
