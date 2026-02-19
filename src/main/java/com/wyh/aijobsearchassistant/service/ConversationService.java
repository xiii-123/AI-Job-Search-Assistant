package com.wyh.aijobsearchassistant.service;

import com.wyh.aijobsearchassistant.dto.ConversationDTO;
import com.wyh.aijobsearchassistant.dto.ConversationRenameRequest;
import com.wyh.aijobsearchassistant.dto.PageRequestDTO;
import com.wyh.aijobsearchassistant.dto.PageResponseDTO;
import com.wyh.aijobsearchassistant.entity.ChatHistory;
import com.wyh.aijobsearchassistant.entity.Conversation;
import com.wyh.aijobsearchassistant.enums.ConversationStatus;
import com.wyh.aijobsearchassistant.exception.BusinessException;
import com.wyh.aijobsearchassistant.repository.ChatHistoryRepository;
import com.wyh.aijobsearchassistant.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 会话管理服务
 * 提供会话的增删改查功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationService {

    private final ConversationRepository conversationRepository;
    private final ChatHistoryRepository chatHistoryRepository;

    /**
     * 查询用户的会话列表（分页+过滤）
     */
    public PageResponseDTO<ConversationDTO> getConversations(
            Long userId,
            String keyword,
            PageRequestDTO pageRequestDTO) {

        log.info("[ConversationService] 查询用户会话列表: userId={}, keyword={}, pageNum={}, pageSize={}",
                userId, keyword, pageRequestDTO.getPageNum(), pageRequestDTO.getPageSize());

        // 构建分页参数（按更新时间倒序）
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPageNum() - 1,
                pageRequestDTO.getPageSize(),
                Sort.by(Sort.Direction.DESC, "updatedAt")
        );

        // 查询会话
        Page<Conversation> page;
        if (keyword == null || keyword.trim().isEmpty()) {
            page = conversationRepository.findByUserIdAndStatusOrderByUpdatedAtDesc(
                    userId, ConversationStatus.ACTIVE.getCode(), pageable);
        } else {
            page = conversationRepository.findByUserIdAndStatusAndNameContainingOrderByUpdatedAtDesc(
                    userId, ConversationStatus.ACTIVE.getCode(), keyword, pageable);
        }

        // 转换为 DTO
        List<ConversationDTO> dtos = page.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        log.info("[ConversationService] 查询到 {} 个会话", page.getTotalElements());

        // 手动构建分页响应
        return PageResponseDTO.<ConversationDTO>builder()
                .pageNum(page.getNumber() + 1)
                .pageSize(page.getSize())
                .total(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .records(dtos)
                .hasNext(page.hasNext())
                .hasPrevious(page.hasPrevious())
                .build();
    }

    /**
     * 创建新会话
     */
    public ConversationDTO createConversation(Long userId, String name) {
        log.info("[ConversationService] 创建新会话: userId={}, name={}", userId, name);

        // 生成唯一会话ID
        String conversationId = UUID.randomUUID().toString();

        Conversation conversation = Conversation.builder()
                .conversationId(conversationId)
                .userId(userId)
                .name(name)
                .status(ConversationStatus.ACTIVE.getCode())
                .messageCount(0)
                .build();

        conversation = conversationRepository.save(conversation);

        log.info("[ConversationService] 会话创建成功: conversationId={}", conversationId);

        return convertToDTO(conversation);
    }

    /**
     * 重命名会话
     */
    @Transactional
    public ConversationDTO renameConversation(Long userId, ConversationRenameRequest request) {
        log.info("[ConversationService] 重命名会话: userId={}, conversationId={}, newName={}",
                userId, request.getConversationId(), request.getNewName());

        // 查询会话并验证归属权
        Conversation conversation = conversationRepository
                .findByConversationIdAndUserId(request.getConversationId(), userId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在或无权限访问"));

        // 更新名称
        conversation.setName(request.getNewName());
        conversation = conversationRepository.save(conversation);

        log.info("[ConversationService] 会话重命名成功: conversationId={}", request.getConversationId());

        return convertToDTO(conversation);
    }

    /**
     * 删除会话（级联删除所有聊天记录和上下文）
     */
    @Transactional
    public void deleteConversation(Long userId, String conversationId) {
        log.info("[ConversationService] 删除会话: userId={}, conversationId={}", userId, conversationId);

        // 查询会话并验证归属权
        Conversation conversation = conversationRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在或无权限访问"));

        // 级联删除所有聊天记录
        List<ChatHistory> histories = chatHistoryRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        chatHistoryRepository.deleteAll(histories);

        // 删除会话
        conversationRepository.delete(conversation);

        log.info("[ConversationService] 会话删除成功: conversationId={}, 删除了 {} 条消息",
                conversationId, histories.size());
    }

    /**
     * 清空会话（保留会话主体，删除所有聊天记录）
     */
    @Transactional
    public void clearConversation(Long userId, String conversationId) {
        log.info("[ConversationService] 清空会话: userId={}, conversationId={}", userId, conversationId);

        // 查询会话并验证归属权
        Conversation conversation = conversationRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在或无权限访问"));

        // 删除所有聊天记录
        List<ChatHistory> histories = chatHistoryRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);
        chatHistoryRepository.deleteAll(histories);

        // 重置消息计数
        conversation.setMessageCount(0);
        conversationRepository.save(conversation);

        log.info("[ConversationService] 会话清空成功: conversationId={}, 清空了 {} 条消息",
                conversationId, histories.size());
    }

    /**
     * 获取会话详情
     */
    public ConversationDTO getConversation(Long userId, String conversationId) {
        log.info("[ConversationService] 获取会话详情: userId={}, conversationId={}", userId, conversationId);

        Conversation conversation = conversationRepository
                .findByConversationIdAndUserId(conversationId, userId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在或无权限访问"));

        return convertToDTO(conversation);
    }

    /**
     * 获取用户的会话总数
     */
    public Long getUserConversationCount(Long userId) {
        return conversationRepository.countByUserIdAndStatus(userId, ConversationStatus.ACTIVE.getCode());
    }

    /**
     * 更新会话消息计数
     */
    @Transactional
    public void incrementMessageCount(String conversationId) {
        conversationRepository.findByConversationId(conversationId).ifPresent(conversation -> {
            conversation.setMessageCount(conversation.getMessageCount() + 1);
            conversationRepository.save(conversation);
        });
    }

    /**
     * 确保会话存在（用于聊天时自动创建会话）
     */
    public Conversation ensureConversationExists(Long userId, String conversationId) {
        return conversationRepository.findByConversationId(conversationId)
                .orElseGet(() -> {
                    // 如果会话不存在，自动创建
                    Conversation newConversation = Conversation.builder()
                            .conversationId(conversationId)
                            .userId(userId)
                            .name("新对话")
                            .status(ConversationStatus.ACTIVE.getCode())
                            .messageCount(0)
                            .build();
                    return conversationRepository.save(newConversation);
                });
    }

    /**
     * 转换为 DTO
     */
    private ConversationDTO convertToDTO(Conversation conversation) {
        return ConversationDTO.builder()
                .conversationId(conversation.getConversationId())
                .name(conversation.getName())
                .messageCount(conversation.getMessageCount())
                .createdAt(conversation.getCreatedAt())
                .updatedAt(conversation.getUpdatedAt())
                .status(conversation.getStatus())
                .build();
    }
}
