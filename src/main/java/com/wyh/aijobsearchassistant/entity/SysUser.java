package com.wyh.aijobsearchassistant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

/**
 * System User Entity
 * Stores user information for authentication and authorization
 */
@Entity
@Table(name = "sys_user", indexes = {
    @Index(name = "idx_username", columnList = "username"),
    @Index(name = "idx_email", columnList = "email")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SysUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Username (unique identifier for login)
     */
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    /**
     * Email address (optional, can be used for login)
     */
    @Column(name = "email", length = 100)
    private String email;

    /**
     * Password (BCrypt encrypted)
     */
    @Column(name = "password", nullable = false, length = 255)
    private String password;

    /**
     * Display name
     */
    @Column(name = "display_name", length = 100)
    private String displayName;

    /**
     * Account status: 0-disabled, 1-enabled
     */
    @Column(name = "status", nullable = false)
    @Builder.Default
    private Integer status = 1;

    /**
     * Role: USER, ADMIN
     */
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private String role = "USER";

    /**
     * Account creation time
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Last update time
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * Last login time
     */
    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    /**
     * Check if account is enabled
     */
    public boolean isEnabled() {
        return status != null && status == 1;
    }
}
