package com.wyh.aijobsearchassistant.controller;

import com.wyh.aijobsearchassistant.entity.SysUser;
import com.wyh.aijobsearchassistant.model.ApiResponse;
import com.wyh.aijobsearchassistant.model.UserLoginRequest;
import com.wyh.aijobsearchassistant.model.UserLoginResponse;
import com.wyh.aijobsearchassistant.model.UserRegisterRequest;
import com.wyh.aijobsearchassistant.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User Controller
 * Handles user registration, login, and logout operations
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor


public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    private final UserService userService;

    /**
     * User registration
     * POST /api/auth/register
     */
    @PostMapping("/register")
    public ApiResponse<SysUser> register(@Valid @RequestBody UserRegisterRequest request) {
        log.info("[UserController] User registration request: {}", request.username());
        SysUser user = userService.register(request);
        return ApiResponse.success("Registration successful", user);
    }

    /**
     * User login
     * POST /api/auth/login
     */
    @PostMapping("/login")
    public ApiResponse<UserLoginResponse> login(@Valid @RequestBody UserLoginRequest request) {
        log.info("[UserController] User login request: {}", request.username());
        UserLoginResponse response = userService.login(request);
        return ApiResponse.success("Login successful", response);
    }

    /**
     * User logout
     * POST /api/auth/logout
     * Since we use JWT stateless authentication, logout is mainly client-side (clear token)
     */
    @PostMapping("/logout")
    public ApiResponse<String> logout() {
        log.info("[UserController] User logout");
        return ApiResponse.success("Logout successful");
    }

    /**
     * Get current user info
     * GET /api/auth/me
     */
    @GetMapping("/me")
    public ApiResponse<SysUser> getCurrentUser() {
        String username = org.springframework.security.core.context.SecurityContextHolder
            .getContext().getAuthentication().getName();

        return userService.getUserByUsername(username)
            .map(user -> ApiResponse.success(user))
            .orElse(ApiResponse.error(404, "User not found"));
    }
}
