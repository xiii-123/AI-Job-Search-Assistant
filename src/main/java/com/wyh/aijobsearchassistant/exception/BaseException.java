package com.wyh.aijobsearchassistant.exception;

/**
 * Base Exception Class (Java 21 Sealed Class Hierarchy)
 * All custom exceptions extend from this base class
 */
public sealed abstract class BaseException extends RuntimeException
    permits BusinessException, SystemException {

    protected final int code;

    public BaseException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BaseException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
