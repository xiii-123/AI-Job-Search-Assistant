package com.wyh.aijobsearchassistant.constant;

/**
 * Status Code Constants
 * Status code definitions for unified API response format
 */
public final class StatusCode {

    private StatusCode() {
        throw new UnsupportedOperationException("Constant class cannot be instantiated");
    }

    // Success
    public static final int SUCCESS = 200;
    public static final String SUCCESS_MSG = "Operation successful";

    // Client Errors (4xx)
    public static final int BAD_REQUEST = 400;
    public static final String BAD_REQUEST_MSG = "Invalid request parameters";

    public static final int UNAUTHORIZED = 401;
    public static final String UNAUTHORIZED_MSG = "Authentication failed";

    public static final int FORBIDDEN = 403;
    public static final String FORBIDDEN_MSG = "Access denied";

    public static final int NOT_FOUND = 404;
    public static final String NOT_FOUND_MSG = "Resource not found";

    public static final int CONFLICT = 409;
    public static final String CONFLICT_MSG = "Resource conflict";

    public static final int TOO_MANY_REQUESTS = 429;
    public static final String TOO_MANY_REQUESTS_MSG = "Too many requests";

    // Server Errors (5xx)
    public static final int INTERNAL_ERROR = 500;
    public static final String INTERNAL_ERROR_MSG = "Internal server error";

    public static final int SERVICE_UNAVAILABLE = 503;
    public static final String SERVICE_UNAVAILABLE_MSG = "Service temporarily unavailable";

    // Business Error Codes
    public static final int USER_NOT_FOUND = 1001;
    public static final String USER_NOT_FOUND_MSG = "User does not exist";

    public static final int USER_ALREADY_EXISTS = 1002;
    public static final String USER_ALREADY_EXISTS_MSG = "User already exists";

    public static final int INVALID_PASSWORD = 1003;
    public static final String INVALID_PASSWORD_MSG = "Incorrect password";

    public static final int INVALID_TOKEN = 1004;
    public static final String INVALID_TOKEN_MSG = "Invalid or expired token";

    public static final int AI_MODEL_ERROR = 2001;
    public static final String AI_MODEL_ERROR_MSG = "AI model invocation failed";

    public static final int AGENT_ERROR = 2002;
    public static final String AGENT_ERROR_MSG = "Agent execution failed";

    public static final int CONTEXT_NOT_FOUND = 2003;
    public static final String CONTEXT_NOT_FOUND_MSG = "Conversation context does not exist";

    public static final int CONFIG_ERROR = 3001;
    public static final String CONFIG_ERROR_MSG = "Configuration error";
}
