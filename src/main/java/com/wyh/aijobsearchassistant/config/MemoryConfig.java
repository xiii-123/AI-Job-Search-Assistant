package com.wyh.aijobsearchassistant.config;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Memory Configuration
 * Configures ChatMemory and MessageChatMemoryAdvisor for multi-turn conversations
 */
@Configuration
public class MemoryConfig {

    /**
     * Configure ChatMemory
     * Uses in-memory storage for conversation context
     * Adapted for Java 21 memory management
     */
    @Bean
    public ChatMemory chatMemory() {
        return new InMemoryChatMemory();
    }

    /**
     * Configure MessageChatMemoryAdvisor
     * Automatically manages conversation history and context
     * Isolates conversations by conversationId and userId
     */
    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return new MessageChatMemoryAdvisor(chatMemory);
    }

    /**
     * Configure QuestionAnswerAdvisor
     * (Reserved for future enhancement with vector database)
     * Will enable RAG (Retrieval Augmented Generation) when combined with PGVector
     */
    @Bean
    public QuestionAnswerAdvisor questionAnswerAdvisor() {
        // Placeholder for future PGVector integration
        // Currently returns null, will be implemented in enhanced version
        return null;
    }
}
