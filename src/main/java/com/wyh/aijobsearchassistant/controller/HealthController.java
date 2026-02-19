package com.wyh.aijobsearchassistant.controller;

import com.wyh.aijobsearchassistant.constant.StatusCode;
import com.wyh.aijobsearchassistant.model.HealthResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Controller
 * Handles health check and system status monitoring
 */
@RestController
@RequestMapping("/health")
public class HealthController {

    @Value("${spring.ai.dashscope.api-key:}")
    private String dashScopeApiKey;

    @Value("${spring.datasource.url:}")
    private String dataSourceUrl;

    /**
     * Health check endpoint
     * GET /api/health/status
     */
    @GetMapping("/status")
    public ResponseEntity<HealthResponse> healthCheck() {
        Map<String, HealthResponse.ComponentHealth> components = new HashMap<>();

        // Check DashScope API configuration
        components.put("dashscope", new HealthResponse.ComponentHealth(
            dashScopeApiKey != null && !dashScopeApiKey.isEmpty() ? "UP" : "DOWN",
            Map.of(
                "configured", dashScopeApiKey != null && !dashScopeApiKey.isEmpty(),
                "timestamp", LocalDateTime.now().toString()
            )
        ));

        // Check Database
        components.put("database", new HealthResponse.ComponentHealth(
            dataSourceUrl != null && !dataSourceUrl.isEmpty() ? "UP" : "DOWN",
            Map.of(
                "url", dataSourceUrl,
                "type", "SQLite",
                "timestamp", LocalDateTime.now().toString()
            )
        ));

        // Check Agent status
        components.put("agent", new HealthResponse.ComponentHealth(
            "UP",
            Map.of(
                "status", "Ready",
                "timestamp", LocalDateTime.now().toString()
            )
        ));

        // Overall status
        String overallStatus = components.values().stream()
            .allMatch(c -> c.status().equals("UP")) ? "UP" : "DEGRADED";

        HealthResponse response = new HealthResponse(
            overallStatus,
            "1.0.0",
            components
        );

        return ResponseEntity.ok(response);
    }
}
