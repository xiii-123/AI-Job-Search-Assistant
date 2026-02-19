package com.wyh.aijobsearchassistant.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring AI Alibaba Configuration
 * Configures DashScope ChatClient and related components
 */
@Configuration
public class AiConfig {

    /**
     * Configure ChatClient
     * Main entry point for AI interactions
     * Uses the ChatModel bean provided by DashScopeAutoConfiguration
     */
    @Bean
    public ChatClient chatClient(ChatModel chatModel) {
        return ChatClient.builder(chatModel).build();
    }
}
