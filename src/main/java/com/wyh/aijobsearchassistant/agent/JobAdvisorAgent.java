package com.wyh.aijobsearchassistant.agent;

import com.wyh.aijobsearchassistant.constant.AgentIntent;
import com.wyh.aijobsearchassistant.constant.SkillIdentifier;
import com.wyh.aijobsearchassistant.exception.AgentException;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * Job Advisor Agent
 * Core Agent implementation following Spring AI Alibaba best practices
 *
 * Core Flow:
 * 1. Intent Recognition: Uses PromptTemplate + DashScope to parse user question intent
 * 2. Skill Matching: Matches intent to preset skill identifier
 * 3. Response Generation: Uses DashScopeChatClient + Skill template to generate response
 */
@Component
@RequiredArgsConstructor


public class JobAdvisorAgent {

    private static final Logger log = LoggerFactory.getLogger(JobAdvisorAgent.class);

    private final ChatModel chatModel;
    private final IntentRecognitionPrompt intentRecognitionPrompt;
    private final SkillMatchTool skillMatchTool;
    private final MessageChatMemoryAdvisor messageChatMemoryAdvisor;

    /**
     * Execute Agent flow
     *
     * @param userMessage User message
     * @param conversationId Conversation ID for context
     * @return Agent response
     */
    public AgentResponse execute(String userMessage, String conversationId) {
        long startTime = System.currentTimeMillis();

        try {
            // Step 1: Intent Recognition
            log.debug("[Agent] Step 1: Intent recognition for message: {}", userMessage);
            AgentIntent intent = recognizeIntent(userMessage);
            log.debug("[Agent] Recognized intent: {}", intent);

            // Step 2: Skill Matching
            log.debug("[Agent] Step 2: Skill matching for intent: {}", intent);
            String skill = matchSkill(intent);
            log.debug("[Agent] Matched skill: {}", skill);

            // Step 3: Response Generation
            log.debug("[Agent] Step 3: Generating response with skill: {}", skill);
            String response = generateResponse(userMessage, intent, skill, conversationId);
            log.debug("[Agent] Generated response");

            long executionTime = System.currentTimeMillis() - startTime;
            log.info("[Agent] Execution completed in {}ms", executionTime);

            return new AgentResponse(
                response,
                conversationId,
                intent.getCode(),
                skill,
                null,
                "qwen-plus",
                executionTime,
                null
            );

        } catch (Exception e) {
            log.error("[Agent] Execution failed", e);
            throw AgentException.agentError("Agent execution failed: " + e.getMessage(), e);
        }
    }

    /**
     * Step 1: Intent Recognition
     * Uses PromptTemplate + LLM to identify user intent
     */
    private AgentIntent recognizeIntent(String userMessage) {
        try {
            String prompt = intentRecognitionPrompt.createPrompt(userMessage);

            // Use ChatClient directly for intent recognition
            ChatClient chatClient = ChatClient.builder(chatModel).build();

            String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

            return intentRecognitionPrompt.parseIntent(response);

        } catch (Exception e) {
            log.warn("Intent recognition failed, defaulting to UNKNOWN", e);
            return AgentIntent.UNKNOWN;
        }
    }

    /**
     * Step 2: Skill Matching
     * Matches intent to corresponding skill identifier
     */
    private String matchSkill(AgentIntent intent) {
        return SkillIdentifier.fromIntent(intent);
    }

    /**
     * Step 3: Response Generation
     * Uses DashScopeChatClient + Skill template to generate response
     */
    private String generateResponse(String userMessage, AgentIntent intent, String skill, String conversationId) {
        // Build skill-specific prompt template
        String systemPrompt = buildSkillSystemPrompt(intent, skill);

        // Create chat client with memory advisor
        ChatClient chatClient = ChatClient.builder(chatModel)
            .defaultAdvisors(messageChatMemoryAdvisor)
            .defaultSystem(systemPrompt)
            .build();

        // Generate response
        return chatClient.prompt()
            .user(userMessage)
            .advisors(a -> a.param("conversation_id", conversationId))
            .call()
            .content();
    }

    /**
     * Build skill-specific system prompt
     * In this basic version, uses hardcoded templates
     * Enhanced version can read from markdown files
     */
    private String buildSkillSystemPrompt(AgentIntent intent, String skill) {
        return switch (intent) {
            case RESUME_OPTIMIZATION -> """
                You are a professional resume optimization consultant.
                Your task is to help users improve their resumes to increase interview chances.
                Please provide specific, actionable advice based on the user's resume content.
                """;

            case INTERVIEW_PREPARATION -> """
                You are a professional interview coach.
                Your task is to help users prepare for job interviews.
                Provide common interview questions, answering strategies, and tips for success.
                """;

            case JOB_SEARCH -> """
                You are a job search strategy expert.
                Your task is to help users find suitable job opportunities.
                Provide job search channels, resume optimization for specific positions, and application strategies.
                """;

            case SALARY_NEGOTIATION -> """
                You are a salary negotiation expert.
                Your task is to help users negotiate better salary packages.
                Provide negotiation strategies, market salary ranges, and communication tips.
                """;

            case CAREER_PLANNING -> """
                You are a career development consultant.
                Your task is to help users plan their career paths.
                Provide career planning advice, skill development recommendations, and growth strategies.
                """;

            case SKILL_ASSESSMENT -> """
                You are a professional skills analyst.
                Your task is to help users assess their professional skills.
                Provide skill gap analysis, learning recommendations, and development plans.
                """;

            case INDUSTRY_CONSULTATION -> """
                You are an industry insights consultant.
                Your task is to provide industry trends and consultation.
                Share industry developments, market outlook, and opportunity analysis.
                """;

            default -> """
                You are a professional job search assistant.
                Your task is to help users with various job search related questions.
                Provide helpful, accurate, and practical advice.
                """;
        };
    }

    /**
     * Agent Response Record
     */
    public record AgentResponse(
        String content,
        String conversationId,
        String intent,
        String skill,
        Object toolResults,
        String model,
        long executionTime,
        Integer totalTokens
    ) {}
}
