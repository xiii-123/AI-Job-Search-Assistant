package com.wyh.aijobsearchassistant.exception;

import com.wyh.aijobsearchassistant.constant.StatusCode;

/**
 * User-related Exception (Java 21 Sealed Class)
 */
public non-sealed class UserException extends BusinessException {

    public UserException(int code, String message) {
        super(code, message);
    }

    public UserException(int code, String message, Throwable cause) {
        super(code, message, cause);
    }

    // Factory methods for common user errors
    public static UserException userNotFound() {
        return new UserException(StatusCode.USER_NOT_FOUND, StatusCode.USER_NOT_FOUND_MSG);
    }

    public static UserException userAlreadyExists() {
        return new UserException(StatusCode.USER_ALREADY_EXISTS, StatusCode.USER_ALREADY_EXISTS_MSG);
    }

    public static UserException invalidPassword() {
        return new UserException(StatusCode.INVALID_PASSWORD, StatusCode.INVALID_PASSWORD_MSG);
    }

    public static UserException invalidToken() {
        return new UserException(StatusCode.INVALID_TOKEN, StatusCode.INVALID_TOKEN_MSG);
    }
}
