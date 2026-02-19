package com.wyh.aijobsearchassistant.constant;

/**
 * Agent Intent Enum
 * Enumeration of user intent types recognized by the Job Advisor Agent
 */
public enum AgentIntent {
    /**
     * Resume optimization intent
     */
    RESUME_OPTIMIZATION("resume_optimization", "Resume optimization and improvement suggestions"),

    /**
     * Interview preparation intent
     */
    INTERVIEW_PREPARATION("interview_preparation", "Interview question preparation and coaching"),

    /**
     * Job search intent
     */
    JOB_SEARCH("job_search", "Job search strategies and recommendations"),

    /**
     * Salary negotiation intent
     */
    SALARY_NEGOTIATION("salary_negotiation", "Salary negotiation strategies and advice"),

    /**
     * Career planning intent
     */
    CAREER_PLANNING("career_planning", "Career path planning and development"),

    /**
     * Skill assessment intent
     */
    SKILL_ASSESSMENT("skill_assessment", "Professional skills assessment and analysis"),

    /**
     * Industry consultation intent
     */
    INDUSTRY_CONSULTATION("industry_consultation", "Industry trends and consultation"),

    /**
     * Unknown/Generic intent
     */
    UNKNOWN("unknown", "Unrecognized intent, generic response");

    private final String code;
    private final String description;

    AgentIntent(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Get intent enum from code string
     */
    public static AgentIntent fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return UNKNOWN;
        }
        for (AgentIntent intent : values()) {
            if (intent.code.equalsIgnoreCase(code.trim())) {
                return intent;
            }
        }
        return UNKNOWN;
    }
}
