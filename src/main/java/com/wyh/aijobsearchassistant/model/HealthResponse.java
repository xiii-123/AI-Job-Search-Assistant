package com.wyh.aijobsearchassistant.model;

import java.util.Map;

/**
 * Health Check Response Record (Java 21)
 */
public record HealthResponse(
    /**
     * Overall health status: UP, DOWN, DEGRADED
     */
    String status,

    /**
     * Application version
     */
    String version,

    /**
     * Components health status
     */
    Map<String, ComponentHealth> components
) {
    /**
     * Component health record
     */
    public record ComponentHealth(
        /**
         * Component status
         */
        String status,

        /**
         * Component details
         */
        Map<String, Object> details
    ) {
    }
}
