package com.wyh.aijobsearchassistant.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Unified API Response Record (Java 21)
 * Standard format for all API responses
 *
 * @param <T> Response data type
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
    /**
     * Response code
     */
    int code,

    /**
     * Response message
     */
    String msg,

    /**
     * Response data
     */
    T data,

    /**
     * Response timestamp
     */
    long timestamp
) {
    /**
     * Create success response with data
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(200, "Operation successful", data, System.currentTimeMillis());
    }

    /**
     * Create success response with custom message and data
     */
    public static <T> ApiResponse<T> success(String msg, T data) {
        return new ApiResponse<>(200, msg, data, System.currentTimeMillis());
    }

    /**
     * Create success response without data
     */
    public static <T> ApiResponse<T> success() {
        return new ApiResponse<>(200, "Operation successful", null, System.currentTimeMillis());
    }

    /**
     * Create error response
     */
    public static <T> ApiResponse<T> error(int code, String msg) {
        return new ApiResponse<>(code, msg, null, System.currentTimeMillis());
    }

    /**
     * Create error response with default error code
     */
    public static <T> ApiResponse<T> error(String msg) {
        return new ApiResponse<>(500, msg, null, System.currentTimeMillis());
    }
}
