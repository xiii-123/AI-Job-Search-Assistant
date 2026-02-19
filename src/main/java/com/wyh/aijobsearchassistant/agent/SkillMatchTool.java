package com.wyh.aijobsearchassistant.agent;

import com.wyh.aijobsearchassistant.constant.AgentIntent;
import com.wyh.aijobsearchassistant.constant.SkillIdentifier;
import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Description;
import org.springframework.stereotype.Component;

import java.util.function.Function;

/**
 * Skill Match Tool
 * Spring AI Alibaba Tool implementation for skill matching
 * Matches user intent to corresponding skill identifier
 */
@Description("Matches user intent to the appropriate skill identifier for job search assistance")
@Component
public class SkillMatchTool implements Function<SkillMatchTool.Request, SkillMatchTool.Response> {

    private final ApplicationContext applicationContext;

    public SkillMatchTool(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Response apply(Request request) {
        AgentIntent intent = AgentIntent.fromCode(request.intent());
        String skill = SkillIdentifier.fromIntent(intent);

        return new Response(skill, intent.getCode(), intent.getDescription());
    }

    /**
     * Request record for skill matching
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record Request(
        String intent
    ) {}

    /**
     * Response record containing matched skill information
     */
    public record Response(
        String skill,
        String intentCode,
        String intentDescription
    ) {}
}
