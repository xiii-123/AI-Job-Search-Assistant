package com.wyh.aijobsearchassistant.validator;

import com.wyh.aijobsearchassistant.constant.StatusCode;
import com.wyh.aijobsearchassistant.exception.SystemException;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Configuration Validator
 * Validates core configuration parameters on startup
 * Uses Java 21 Validation API for enhanced validation
 */
@Component
public class ConfigValidator {

    private static final Logger log = LoggerFactory.getLogger(ConfigValidator.class);

    @Value("${spring.ai.dashscope.api-key:}")
    private String dashScopeApiKey;

    @Value("${spring.datasource.url:}")
    private String dataSourceUrl;

    @Value("${spring.security.jwt.secret:}")
    private String jwtSecret;

    /**
     * Validate configuration on startup
     * Terminates application if critical configuration is missing
     */
    @PostConstruct
    public void validateConfig() {
        log.info("[ConfigValidator] Validating application configuration...");

        // Validate DashScope API Key
        if (dashScopeApiKey == null || dashScopeApiKey.trim().isEmpty() ||
            dashScopeApiKey.equals("your-api-key") || dashScopeApiKey.length() < 20) {
            String errorMsg = "DashScope API Key is not configured. Please set DASHSCOPE_API_KEY environment variable.";
            log.error("[ConfigValidator] {}", errorMsg);
            throw new SystemException(StatusCode.CONFIG_ERROR, errorMsg);
        }
        log.info("[ConfigValidator] DashScope API Key: configured");

        // Validate Data Source
        if (dataSourceUrl == null || dataSourceUrl.trim().isEmpty()) {
            String errorMsg = "Data source URL is not configured.";
            log.error("[ConfigValidator] {}", errorMsg);
            throw new SystemException(StatusCode.CONFIG_ERROR, errorMsg);
        }
        log.info("[ConfigValidator] Data Source: {}", dataSourceUrl);

        // Validate JWT Secret
        if (jwtSecret == null || jwtSecret.trim().isEmpty() ||
            jwtSecret.equals("your-secret-key-change-in-production-environment-at-least-256-bits") ||
            jwtSecret.length() < 32) {
            log.warn("[ConfigValidator] JWT Secret is not configured properly. Using default secret. " +
                "Please set JWT_SECRET environment variable in production.");
        } else {
            log.info("[ConfigValidator] JWT Secret: configured");
        }

        log.info("[ConfigValidator] Configuration validation completed successfully.");
    }
}
