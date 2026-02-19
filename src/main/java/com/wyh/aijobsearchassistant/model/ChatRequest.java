package com.wyh.aijobsearchassistant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Chat Request Record (Java 21)
 */
public record ChatRequest(
    /**
     * User message content (required, max 2000 characters)
     */
    @NotBlank(message = "Message content cannot be empty")
    @Size(max = 2000, message = "Message content cannot exceed 2000 characters")
    String message,

    /**
     * Conversation ID (optional, for multi-turn conversation context)
     * If empty, a new conversation ID will be generated
     */
    String conversationId,

    /**
     * Stream response (default false)
     */
    boolean stream
) {
}
