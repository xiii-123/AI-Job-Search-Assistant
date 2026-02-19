package com.wyh.aijobsearchassistant.repository;

import com.wyh.aijobsearchassistant.entity.SysUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * User Repository Interface
 * JPA repository for user data operations
 */
@Repository
public interface UserRepository extends JpaRepository<SysUser, Long> {

    /**
     * Find user by username
     */
    Optional<SysUser> findByUsername(String username);

    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
}
