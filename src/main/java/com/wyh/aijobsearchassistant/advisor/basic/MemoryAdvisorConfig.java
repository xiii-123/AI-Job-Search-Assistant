package com.wyh.aijobsearchassistant.advisor.basic;

import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.context.annotation.Configuration;

/**
 * Memory Advisor Configuration
 * Configures basic memory advisor for multi-turn conversations
 */
@Configuration
public class MemoryAdvisorConfig {

    private final MessageChatMemoryAdvisor messageChatMemoryAdvisor;

    public MemoryAdvisorConfig(MessageChatMemoryAdvisor messageChatMemoryAdvisor) {
        this.messageChatMemoryAdvisor = messageChatMemoryAdvisor;
    }

    /**
     * Get MessageChatMemoryAdvisor bean
     * Used by ChatService and AgentService to maintain conversation context
     */
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor() {
        return messageChatMemoryAdvisor;
    }
}
