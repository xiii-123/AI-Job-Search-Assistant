package com.wyh.aijobsearchassistant.service;

import com.wyh.aijobsearchassistant.entity.SysUser;
import com.wyh.aijobsearchassistant.exception.BusinessException;
import com.wyh.aijobsearchassistant.exception.UserException;
import com.wyh.aijobsearchassistant.model.UserLoginRequest;
import com.wyh.aijobsearchassistant.model.UserLoginResponse;
import com.wyh.aijobsearchassistant.model.UserRegisterRequest;
import com.wyh.aijobsearchassistant.repository.UserRepository;
import com.wyh.aijobsearchassistant.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Optional;

/**
 * User Service
 * Handles user registration, login, logout, and authentication operations
 * Adapted for Java 21 Stream API optimization
 */
@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

    /**
     * Register new user
     */
    @Transactional
    public SysUser register(UserRegisterRequest request) {
        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            throw UserException.userAlreadyExists();
        }

        // Check if email already exists (if provided)
        if (request.email() != null && !request.email().isEmpty()) {
            if (userRepository.existsByEmail(request.email())) {
                throw new BusinessException(409, "Email already registered");
            }
        }

        // Create new user
        SysUser user = SysUser.builder()
            .username(request.username())
            .email(request.email())
            .password(passwordEncoder.encode(request.password()))
            .displayName(request.displayName() != null ? request.displayName() : request.username())
            .status(1)
            .role("USER")
            .build();

        SysUser savedUser = userRepository.save(user);
        log.info("User registered successfully: {}", savedUser.getUsername());

        return savedUser;
    }

    /**
     * User login
     */
    public UserLoginResponse login(UserLoginRequest request) {
        // Find user by username
        SysUser user = userRepository.findByUsername(request.username())
            .orElseThrow(UserException::userNotFound);

        // Verify password
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw UserException.invalidPassword();
        }

        // Check if account is enabled
        if (!user.isEnabled()) {
            throw new BusinessException(403, "Account is disabled");
        }

        // Generate JWT token
        String token = jwtTokenProvider.generateToken(user.getUsername());

        // Update last login time
        user.setLastLoginAt(java.time.LocalDateTime.now());
        userRepository.save(user);

        log.info("User logged in successfully: {}", user.getUsername());

        return UserLoginResponse.of(
            token,
            user.getId(),
            user.getUsername(),
            user.getDisplayName(),
            user.getRole()
        );
    }

    /**
     * Get user by ID
     */
    public Optional<SysUser> getUserById(Long userId) {
        return userRepository.findById(userId);
    }

    /**
     * Get user by username
     */
    public Optional<SysUser> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Load user by username (for Spring Security)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .authorities(new ArrayList<>()) // No roles for now
            .disabled(!user.isEnabled())
            .build();
    }

    /**
     * Verify JWT token and get username
     */
    public String verifyTokenAndGetUsername(String token) {
        return jwtTokenProvider.getUsernameFromToken(token);
    }
}
