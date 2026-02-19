package com.wyh.aijobsearchassistant.exception;

import com.wyh.aijobsearchassistant.constant.StatusCode;

/**
 * Agent-related Exception (Java 21 Sealed Class)
 */
public non-sealed class AgentException extends BusinessException {

    public AgentException(int code, String message) {
        super(code, message);
    }

    public AgentException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    // Factory methods for common agent errors
    public static AgentException agentError(String message) {
        return new AgentException(StatusCode.AGENT_ERROR, message);
    }

    public static AgentException agentError(String message, Throwable cause) {
        return new AgentException(StatusCode.AGENT_ERROR, message, cause);
    }
}
