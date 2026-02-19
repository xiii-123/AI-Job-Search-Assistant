package com.wyh.aijobsearchassistant.agent;

import com.wyh.aijobsearchassistant.constant.AgentIntent;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Intent Recognition Prompt Template
 * Uses Spring AI Alibaba PromptTemplate for intent recognition
 */
@Component
public class IntentRecognitionPrompt {

    private static final String INTENT_RECOGNITION_TEMPLATE = """
        You are an intent recognition assistant for a job search AI system.
        Your task is to analyze the user's question and identify their intent.

        User question: {userQuestion}

        Please identify the user's intent from the following options:
        1. resume_optimization - Resume optimization and improvement suggestions
        2. interview_preparation - Interview question preparation and coaching
        3. job_search - Job search strategies and recommendations
        4. salary_negotiation - Salary negotiation strategies and advice
        5. career_planning - Career path planning and development
        6. skill_assessment - Professional skills assessment and analysis
        7. industry_consultation - Industry trends and consultation
        8. unknown - Unrecognized intent

        Respond with only the intent code (e.g., "resume_optimization").
        """;

    /**
     * Create intent recognition prompt
     */
    public String createPrompt(String userQuestion) {
        return INTENT_RECOGNITION_TEMPLATE.replace("{userQuestion}", userQuestion);
    }

    /**
     * Parse intent from LLM response
     */
    public AgentIntent parseIntent(String response) {
        if (response == null || response.trim().isEmpty()) {
            return AgentIntent.UNKNOWN;
        }

        String intentCode = response.toLowerCase().trim()
            .replace(" ", "")
            .replace("\"", "")
            .replace("'", "")
            .replace(".", "");

        return AgentIntent.fromCode(intentCode);
    }
}
