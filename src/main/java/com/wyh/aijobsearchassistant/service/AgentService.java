package com.wyh.aijobsearchassistant.service;

import com.wyh.aijobsearchassistant.agent.JobAdvisorAgent;
import com.wyh.aijobsearchassistant.entity.ChatHistory;
import com.wyh.aijobsearchassistant.exception.AgentException;
import com.wyh.aijobsearchassistant.model.AgentRequest;
import com.wyh.aijobsearchassistant.model.AgentResponse;
import com.wyh.aijobsearchassistant.repository.ChatHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

/**
 * Agent Service
 * Handles Agent orchestration and execution
 */
@Service
@RequiredArgsConstructor


public class AgentService {
    private static final Logger log = LoggerFactory.getLogger(AgentService.class);

    private final JobAdvisorAgent jobAdvisorAgent;
    private final ChatHistoryRepository chatHistoryRepository;

    /**
     * Execute Agent interaction
     */
    @Transactional
    public AgentResponse executeAgent(AgentRequest request) {
        // Get current user
        Long userId = getCurrentUserId();

        // Generate or use conversation ID
        String conversationId = request.conversationId() != null
            ? request.conversationId()
            : UUID.randomUUID().toString();

        try {
            log.info("[Agent] User: {}, Conversation: {}, Message: {}", userId, conversationId, request.message());

            // Execute Agent
            JobAdvisorAgent.AgentResponse agentResponse = jobAdvisorAgent.execute(
                request.message(),
                conversationId
            );

            // Save conversation to database
            saveConversation(userId, conversationId, request.message(), agentResponse);

            log.info("[Agent] Execution completed successfully");

            return new AgentResponse(
                agentResponse.content(),
                agentResponse.conversationId(),
                agentResponse.intent(),
                agentResponse.skill(),
                agentResponse.toolResults() != null ? (java.util.Map<String, Object>) agentResponse.toolResults() : null,
                agentResponse.model(),
                agentResponse.executionTime(),
                agentResponse.totalTokens()
            );

        } catch (Exception e) {
            log.error("[Agent] Execution failed", e);
            throw AgentException.agentError("Agent execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Clear Agent context
     */
    @Transactional
    public void clearContext(String conversationId) {
        Long userId = getCurrentUserId();
        log.info("[Agent] Clearing context for user: {}, conversation: {}", userId, conversationId);

        chatHistoryRepository.deleteByUserIdAndConversationId(userId, conversationId);
    }

    /**
     * Get current user ID from security context
     */
    private Long getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw AgentException.agentError("User not authenticated");
        }

        // For now, return 1L as placeholder
        // In production, should get actual user ID from user service
        return 1L;
    }

    /**
     * Save conversation to database
     */
    private void saveConversation(Long userId, String conversationId, String userMessage,
                                   JobAdvisorAgent.AgentResponse agentResponse) {
        // Save user message
        ChatHistory userHistory = ChatHistory.builder()
            .userId(userId)
            .conversationId(conversationId)
            .role("USER")
            .content(userMessage)
            .intent(agentResponse.intent())
            .skill(agentResponse.skill())
            .model(agentResponse.model())
            .build();

        chatHistoryRepository.save(userHistory);

        // Save assistant response
        ChatHistory assistantHistory = ChatHistory.builder()
            .userId(userId)
            .conversationId(conversationId)
            .role("ASSISTANT")
            .content(agentResponse.content())
            .intent(agentResponse.intent())
            .skill(agentResponse.skill())
            .model(agentResponse.model())
            .build();

        chatHistoryRepository.save(assistantHistory);
    }
}
