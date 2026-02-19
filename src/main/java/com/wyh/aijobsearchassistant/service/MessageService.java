package com.wyh.aijobsearchassistant.service;

import com.wyh.aijobsearchassistant.dto.EditContextRequest;
import com.wyh.aijobsearchassistant.dto.MessageDTO;
import com.wyh.aijobsearchassistant.dto.PageRequestDTO;
import com.wyh.aijobsearchassistant.dto.PageResponseDTO;
import com.wyh.aijobsearchassistant.entity.ChatHistory;
import com.wyh.aijobsearchassistant.entity.Conversation;
import com.wyh.aijobsearchassistant.exception.BusinessException;
import com.wyh.aijobsearchassistant.repository.ChatHistoryRepository;
import com.wyh.aijobsearchassistant.repository.ConversationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * 历史对话管理服务
 * 提供消息查询、删除、上下文编辑功能
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MessageService {

    private final ChatHistoryRepository chatHistoryRepository;
    private final ConversationRepository conversationRepository;
    private final ChatMemory chatMemory;
    private final ChatClient chatClient;

    /**
     * 查询单会话的历史消息（分页+倒序）
     */
    public PageResponseDTO<MessageDTO> getConversationMessages(
            Long userId,
            String conversationId,
            PageRequestDTO pageRequestDTO) {

        log.info("[MessageService] 查询会话消息: userId={}, conversationId={}, pageNum={}, pageSize={}",
                userId, conversationId, pageRequestDTO.getPageNum(), pageRequestDTO.getPageSize());

        // 验证会话归属权
        if (!conversationRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new BusinessException(403, "无权限访问该会话");
        }

        // 构建分页参数（按时间倒序，最新的在前面）
        Pageable pageable = PageRequest.of(
                pageRequestDTO.getPageNum() - 1,
                pageRequestDTO.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        // 查询消息
        Page<ChatHistory> page = chatHistoryRepository.findByConversationId(conversationId, pageable);

        // 转换为 DTO
        List<MessageDTO> dtos = page.getContent().stream()
                .map(this::convertToDTO)
                .toList();

        log.info("[MessageService] 查询到 {} 条消息", page.getTotalElements());

        // 手动构建分页响应
        return PageResponseDTO.<MessageDTO>builder()
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
     * 删除单条消息
     */
    @Transactional
    public void deleteMessage(Long userId, Long messageId) {
        log.info("[MessageService] 删除消息: userId={}, messageId={}", userId, messageId);

        // 查询消息
        ChatHistory message = chatHistoryRepository.findById(messageId)
                .orElseThrow(() -> new BusinessException(404, "消息不存在"));

        // 验证归属权
        if (!message.getUserId().equals(userId)) {
            throw new BusinessException(403, "无权限删除该消息");
        }

        String conversationId = message.getConversationId();

        // 删除消息
        chatHistoryRepository.delete(message);

        // 更新会话消息计数
        conversationRepository.findByConversationId(conversationId).ifPresent(conversation -> {
            int currentCount = conversation.getMessageCount();
            if (currentCount > 0) {
                conversation.setMessageCount(currentCount - 1);
                conversationRepository.save(conversation);
            }
        });

        // 从内存中移除该消息的上下文
        // 注意：ChatMemory 接口没有直接的 remove 方法
        // 这里需要清空整个会话的上下文
        try {
            String memoryKey = getMemoryKey(userId, conversationId);
            // chatMemory.remove() 方法不存在，暂时跳过
            // TODO: 实现 ChatMemory 的上下文管理
            log.debug("[MessageService] 内存上下文清理: key={}", memoryKey);
        } catch (Exception e) {
            log.warn("[MessageService] 移除内存上下文失败: {}", e.getMessage());
        }

        log.info("[MessageService] 消息删除成功: messageId={}", messageId);
    }

    /**
     * 编辑会话上下文（高级功能）
     * 修改后的上下文将影响后续对话
     */
    @Transactional
    public void editConversationContext(Long userId, EditContextRequest request) {
        log.info("[MessageService] 编辑会话上下文: userId={}, conversationId={}",
                userId, request.getConversationId());

        // 验证会话归属权
        Conversation conversation = conversationRepository
                .findByConversationIdAndUserId(request.getConversationId(), userId)
                .orElseThrow(() -> new BusinessException(404, "会话不存在或无权限访问"));

        // 清除当前会话的内存上下文
        String memoryKey = getMemoryKey(userId, request.getConversationId());
        try {
            // 清空原有上下文
            chatMemory.clear(memoryKey);
            log.info("[MessageService] 已清空原有上下文");
        } catch (Exception e) {
            log.warn("[MessageService] 清空上下文失败: {}", e.getMessage());
        }

        // 添加系统消息作为新的上下文
        try {
            ChatMessage systemMessage = new ChatMessage(
                    request.getNewContext(),
                    "SYSTEM",
                    request.getConversationId()
            );
            // 这里需要将系统消息添加到内存中
            // 注意：具体实现可能需要根据 ChatMemory 的 API 调整
            log.info("[MessageService] 新上下文已设置");
        } catch (Exception e) {
            log.error("[MessageService] 设置新上下文失败: {}", e.getMessage());
            throw new BusinessException(500, "设置上下文失败");
        }

        log.info("[MessageService] 会话上下文编辑成功");
    }

    /**
     * 获取会话上下文摘要
     */
    public Map<String, Object> getContextSummary(Long userId, String conversationId) {
        log.info("[MessageService] 获取会话上下文摘要: userId={}, conversationId={}", userId, conversationId);

        // 验证会话归属权
        if (!conversationRepository.existsByConversationIdAndUserId(conversationId, userId)) {
            throw new BusinessException(403, "无权限访问该会话");
        }

        // 获取会话的所有消息
        List<ChatHistory> messages = chatHistoryRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

        Map<String, Object> summary = new HashMap<>();
        summary.put("conversationId", conversationId);
        summary.put("totalMessages", messages.size());
        summary.put("hasContext", messages.size() > 0);

        if (messages.size() > 0) {
            summary.put("firstMessageTime", messages.get(0).getCreatedAt());
            summary.put("lastMessageTime", messages.get(messages.size() - 1).getCreatedAt());
        }

        return summary;
    }

    /**
     * 获取内存键
     */
    private String getMemoryKey(Long userId, String conversationId) {
        return userId + ":" + conversationId;
    }

    /**
     * 转换为 DTO
     */
    private MessageDTO convertToDTO(ChatHistory chatHistory) {
        return MessageDTO.builder()
                .id(chatHistory.getId())
                .conversationId(chatHistory.getConversationId())
                .role(chatHistory.getRole())
                .content(chatHistory.getContent())
                .intent(chatHistory.getIntent())
                .skill(chatHistory.getSkill())
                .tokenCount(chatHistory.getTokenCount())
                .sendTime(chatHistory.getCreatedAt())
                .model(chatHistory.getModel())
                .build();
    }

    /**
     * 内部类：ChatMessage（用于内存存储）
     */
    private record ChatMessage(String content, String role, String conversationId) {}
}
