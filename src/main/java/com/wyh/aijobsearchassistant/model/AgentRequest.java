package com.wyh.aijobsearchassistant.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Agent Interaction Request Record (Java 21)
 */
public record AgentRequest(
    /**
     * User message/question (required, max 2000 characters)
     */
    @NotBlank(message = "Message content cannot be empty")
    @Size(max = 2000, message = "Message content cannot exceed 2000 characters")
    String message,

    /**
     * Conversation ID (optional, for multi-turn conversation context)
     */
    String conversationId,

    /**
     * Specify intent (optional, auto-recognition if not specified)
     */
    String intent,

    /**
     * Specify skill (optional, auto-matching if not specified)
     */
    String skill,

    /**
     * Stream response (default false)
     */
    boolean stream
) {
}
