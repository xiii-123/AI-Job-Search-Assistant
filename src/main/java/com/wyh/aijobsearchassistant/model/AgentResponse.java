package com.wyh.aijobsearchassistant.model;

import java.util.Map;

/**
 * Agent Response Record (Java 21)
 */
public record AgentResponse(
    /**
     * AI response content
     */
    String content,

    /**
     * Conversation ID
     */
    String conversationId,

    /**
     * Recognized intent type
     */
    String intent,

    /**
     * Used skill identifier
     */
    String skill,

    /**
     * Tool call results (if any tools were called)
     */
    Map<String, Object> toolResults,

    /**
     * Model name used
     */
    String model,

    /**
     * Execution time in milliseconds
     */
    Long executionTime,

    /**
     * Total tokens consumed
     */
    Integer totalTokens
) {
}
