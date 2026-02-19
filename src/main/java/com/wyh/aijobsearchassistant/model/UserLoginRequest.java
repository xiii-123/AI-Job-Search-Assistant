package com.wyh.aijobsearchassistant.model;

import jakarta.validation.constraints.NotBlank;

/**
 * User Login Request Record (Java 21)
 */
public record UserLoginRequest(
    /**
     * Username (required)
     */
    @NotBlank(message = "Username cannot be empty")
    String username,

    /**
     * Password (required)
     */
    @NotBlank(message = "Password cannot be empty")
    String password
) {
}
