package com.wyh.aijobsearchassistant.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * User Registration Request Record (Java 21)
 */
public record UserRegisterRequest(
    /**
     * Username (required, 3-50 characters)
     */
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 50, message = "Username must be 3-50 characters")
    String username,

    /**
     * Password (required, 6-100 characters)
     */
    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, max = 100, message = "Password must be 6-100 characters")
    String password,

    /**
     * Email (optional)
     */
    @Email(message = "Invalid email format")
    String email,

    /**
     * Display name (optional)
     */
    String displayName
) {
}
