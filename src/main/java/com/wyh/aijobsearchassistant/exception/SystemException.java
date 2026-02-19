package com.wyh.aijobsearchassistant.exception;

/**
 * System Exception (Java 21 Sealed Class)
 * Exceptions caused by system errors (e.g., database errors, configuration errors)
 */
public non-sealed class SystemException extends BaseException {

    public SystemException(int code, String message) {
        super(code, message);
    }

    public SystemException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }
}
