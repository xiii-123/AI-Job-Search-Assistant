package com.wyh.aijobsearchassistant.model;

/**
 * Chat Response Record (Java 21)
 */
public record ChatResponse(
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
     * Model name used
     */
    String model,

    /**
     * Total tokens consumed
     */
    Integer totalTokens
) {
}
