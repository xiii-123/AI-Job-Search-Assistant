package com.wyh.aijobsearchassistant.constant;

/**
 * Skill Identifier Constants
 * Identifiers for various skills in the Job Advisor Agent system
 */
public final class SkillIdentifier {

    private SkillIdentifier() {
        throw new UnsupportedOperationException("Constant class cannot be instantiated");
    }

    // Core Skills
    public static final String RESUME_OPTIMIZER = "resume_optimizer";
    public static final String RESUME_REVIEWER = "resume_reviewer";

    public static final String INTERVIEW_COACH = "interview_coach";
    public static final String INTERVIEW_QUESTIONS = "interview_questions";

    public static final String JOB_SEARCHER = "job_searcher";
    public static final String JOB_MATCHER = "job_matcher";

    public static final String SALARY_ADVISOR = "salary_advisor";
    public static final String SALARY_CALCULATOR = "salary_calculator";

    public static final String CAREER_PLANNER = "career_planner";
    public static final String CAREER_PATH_ANALYZER = "career_path_analyzer";

    public static final String SKILL_ANALYZER = "skill_analyzer";
    public static final String SKILL_RECOMMENDATION = "skill_recommendation";

    public static final String INDUSTRY_INSIGHTS = "industry_insights";
    public static final String MARKET_ANALYZER = "market_analyzer";

    // Generic fallback skill
    public static final String GENERAL_ASSISTANT = "general_assistant";

    // Skill Categories
    public static final String CATEGORY_RESUME = "resume";
    public static final String CATEGORY_INTERVIEW = "interview";
    public static final String CATEGORY_JOB_SEARCH = "job_search";
    public static final String CATEGORY_SALARY = "salary";
    public static final String CATEGORY_CAREER = "career";
    public static final String CATEGORY_SKILL = "skill";
    public static final String CATEGORY_INDUSTRY = "industry";
    public static final String CATEGORY_GENERAL = "general";

    /**
     * Get skill identifier based on intent
     */
    public static String fromIntent(AgentIntent intent) {
        return switch (intent) {
            case RESUME_OPTIMIZATION -> RESUME_OPTIMIZER;
            case INTERVIEW_PREPARATION -> INTERVIEW_COACH;
            case JOB_SEARCH -> JOB_SEARCHER;
            case SALARY_NEGOTIATION -> SALARY_ADVISOR;
            case CAREER_PLANNING -> CAREER_PLANNER;
            case SKILL_ASSESSMENT -> SKILL_ANALYZER;
            case INDUSTRY_CONSULTATION -> INDUSTRY_INSIGHTS;
            default -> GENERAL_ASSISTANT;
        };
    }
}
