package com.wyh.aijobsearchassistant.service;

import com.wyh.aijobsearchassistant.entity.ChatHistory;
import com.wyh.aijobsearchassistant.exception.ChatException;
import com.wyh.aijobsearchassistant.model.ChatRequest;
import com.wyh.aijobsearchassistant.model.ChatResponse;
import com.wyh.aijobsearchassistant.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

/**
 * Chat Service
 * Handles LLM chat operations and memory management
 */
@Service
@RequiredArgsConstructor


public class ChatService {

    private static final Logger log = LoggerFactory.getLogger(ChatService.class);

    private final ChatModel chatModel;
    private final MessageChatMemoryAdvisor messageChatMemoryAdvisor;
    private final ChatHistoryRepository chatHistoryRepository;

    /**
     * Chat with LLM
     */
    @Transactional
    public ChatResponse chat(ChatRequest request) {
        // Get current user
        Long userId = getCurrentUserId();

        // Generate or use conversation ID
        String conversationId = request.conversationId() != null
            ? request.conversationId()
            : UUID.randomUUID().toString();

        try {
            log.info("[Chat] User: {}, Conversation: {}, Message: {}", userId, conversationId, request.message());

            // Save user message
            saveUserMessage(userId, conversationId, request.message());

            // Create chat client with memory advisor
            ChatClient chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(messageChatMemoryAdvisor)
                .build();

            // Get AI response
            long startTime = System.currentTimeMillis();
            String aiResponse = chatClient.prompt()
                .user(request.message())
                .advisors(a -> a
                    .param("conversation_id", conversationId)
                    .param("user_id", userId.toString())
                )
                .call()
                .content();

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("[Chat] Response generated in {}ms", executionTime);

            // Save assistant message
            saveAssistantMessage(userId, conversationId, aiResponse);

            return new ChatResponse(
                aiResponse,
                conversationId,
                null,  // intent
                null,  // skill
                "qwen-plus",
                null   // tokens
            );

        } catch (Exception e) {
            log.error("[Chat] Error occurred", e);
            throw ChatException.aiModelError("Chat failed: " + e.getMessage(), e);
        }
    }

    /**
     * Clear conversation context
     */
    @Transactional
    public void clearContext(String conversationId) {
        Long userId = getCurrentUserId();
        log.info("[Chat] Clearing context for user: {}, conversation: {}", userId, conversationId);

        // Delete conversation history from database
        chatHistoryRepository.deleteByUserIdAndConversationId(userId, conversationId);

        // Note: In-memory chat memory will be cleared when advisor purges old messages
        // For complete reset, we may need to implement a method to clear from memory advisor
    }

    /**
     * Get conversation history
     */
    public List<ChatHistory> getConversationHistory(String conversationId) {
        Long userId = getCurrentUserId();
        return chatHistoryRepository.findByUserIdAndConversationIdOrderByCreatedAtAsc(userId, conversationId);
    }

    /**
     * Get all conversations for current user
     */
    public List<ChatHistory> getAllConversations() {
        Long userId = getCurrentUserId();
        return chatHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw ChatException.aiModelError("User not authenticated");
        }

        String username = authentication.getName();
        return chatHistoryRepository.findDistinctConversationIdByUserIdOrderByCreatedAtDesc(1L)
            .stream()
            .findFirst()
            .map(s -> 1L) // Placeholder - should get from user service
            .orElseThrow(() -> ChatException.aiModelError("User not found"));
    }

    /**
     * Save user message to database
     */
    private void saveUserMessage(Long userId, String conversationId, String content) {
        ChatHistory history = ChatHistory.builder()
            .userId(userId)
            .conversationId(conversationId)
            .role("USER")
            .content(content)
            .model("qwen-plus")
            .build();

        chatHistoryRepository.save(history);
    }

    /**
     * Save assistant message to database
     */
    private void saveAssistantMessage(Long userId, String conversationId, String content) {
        ChatHistory history = ChatHistory.builder()
            .userId(userId)
            .conversationId(conversationId)
            .role("ASSISTANT")
            .content(content)
            .model("qwen-plus")
            .build();

        chatHistoryRepository.save(history);
    }
}
