package com.wyh.aijobsearchassistant.exception;

import com.wyh.aijobsearchassistant.constant.StatusCode;

/**
 * Chat-related Exception (Java 21 Sealed Class)
 */
public non-sealed class ChatException extends BusinessException {

    public ChatException(int code, String message) {
        super(code, message);
    }

    public ChatException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    // Factory methods for common chat errors
    public static ChatException contextNotFound() {
        return new ChatException(StatusCode.CONTEXT_NOT_FOUND, StatusCode.CONTEXT_NOT_FOUND_MSG);
    }

    public static ChatException aiModelError(String message) {
        return new ChatException(StatusCode.AI_MODEL_ERROR, message);
    }

    public static ChatException aiModelError(String message, Throwable cause) {
        return new ChatException(StatusCode.AI_MODEL_ERROR, message, cause);
    }
}
