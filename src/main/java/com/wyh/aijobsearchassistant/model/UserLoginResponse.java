package com.wyh.aijobsearchassistant.model;

/**
 * User Login Response Record (Java 21)
 */
public record UserLoginResponse(
    /**
     * JWT access token
     */
    String token,

    /**
     * Token type (Bearer)
     */
    String tokenType,

    /**
     * User ID
     */
    Long userId,

    /**
     * Username
     */
    String username,

    /**
     * Display name
     */
    String displayName,

    /**
     * User role
     */
    String role
) {
    public static UserLoginResponse of(String token, Long userId, String username,
                                       String displayName, String role) {
        return new UserLoginResponse(token, "Bearer", userId, username, displayName, role);
    }
}
