package com.wyh.aijobsearchassistant;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AI Job Search Assistant Application
 * Main application class for the AI Job Assistant based on Spring AI Alibaba
 *
 * Features:
 * - User authentication with JWT
 * - LLM chat with multi-turn conversation support
 * - Agent orchestration for job search assistance
 * - SQLite local database
 * - Health monitoring
 */
@SpringBootApplication
public class AiJobSearchAssistantApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiJobSearchAssistantApplication.class, args);
    }

}
