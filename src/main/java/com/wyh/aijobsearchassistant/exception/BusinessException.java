package com.wyh.aijobsearchassistant.exception;

/**
 * Business Exception (Java 21 Sealed Class)
 * Exceptions caused by business logic errors (e.g., user not found, invalid credentials)
 */
public sealed class BusinessException extends BaseException
    permits UserException, ChatException, AgentException {

    public BusinessException(int code, String message) {
        super(code, message);
    }

    public BusinessException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
