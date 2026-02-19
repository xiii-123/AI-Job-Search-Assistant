package com.wyh.aijobsearchassistant.controller;

import com.wyh.aijobsearchassistant.entity.ChatHistory;
import com.wyh.aijobsearchassistant.model.ApiResponse;
import com.wyh.aijobsearchassistant.model.AgentRequest;
import com.wyh.aijobsearchassistant.model.AgentResponse;
import com.wyh.aijobsearchassistant.model.ChatRequest;
import com.wyh.aijobsearchassistant.model.ChatResponse;
import com.wyh.aijobsearchassistant.service.AgentService;
import com.wyh.aijobsearchassistant.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Chat Controller
 * Handles LLM chat and Agent interaction operations
 */
@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor


public class ChatController {
    private static final Logger log = LoggerFactory.getLogger(ChatController.class);

    private final ChatService chatService;
    private final AgentService agentService;

    /**
     * Simple LLM chat
     * POST /api/chat/send
     */
    @PostMapping("/send")
    public ApiResponse<ChatResponse> chat(@Valid @RequestBody ChatRequest request) {
        log.info("[ChatController] Chat request: {}", request.message());
        ChatResponse response = chatService.chat(request);
        return ApiResponse.success(response);
    }

    /**
     * Agent interaction
     * POST /api/chat/agent
     */
    @PostMapping("/agent")
    public ApiResponse<AgentResponse> agent(@Valid @RequestBody AgentRequest request) {
        log.info("[ChatController] Agent request: {}", request.message());
        AgentResponse response = agentService.executeAgent(request);
        return ApiResponse.success(response);
    }

    /**
     * Clear conversation context
     * DELETE /api/chat/context/{conversationId}
     */
    @DeleteMapping("/context/{conversationId}")
    public ApiResponse<String> clearContext(@PathVariable String conversationId) {
        log.info("[ChatController] Clear context request: {}", conversationId);
        chatService.clearContext(conversationId);
        agentService.clearContext(conversationId);
        return ApiResponse.success("Context cleared successfully");
    }

    /**
     * Get conversation history
     * GET /api/chat/history/{conversationId}
     */
    @GetMapping("/history/{conversationId}")
    public ApiResponse<List<ChatHistory>> getConversationHistory(@PathVariable String conversationId) {
        log.info("[ChatController] Get history request: {}", conversationId);
        List<ChatHistory> history = chatService.getConversationHistory(conversationId);
        return ApiResponse.success(history);
    }

    /**
     * Get all conversations
     * GET /api/chat/conversations
     */
    @GetMapping("/conversations")
    public ApiResponse<List<ChatHistory>> getAllConversations() {
        log.info("[ChatController] Get all conversations request");
        List<ChatHistory> conversations = chatService.getAllConversations();
        return ApiResponse.success(conversations);
    }
}
