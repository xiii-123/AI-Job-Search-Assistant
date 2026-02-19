package com.wyh.aijobsearchassistant.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Chat History Entity
 * Stores conversation history between users and AI
 */
@Entity
@Table(name = "chat_history", indexes = {
    @Index(name = "idx_user_conversation", columnList = "userId,conversationId"),
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * User ID (associated with sys_user)
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * Conversation ID (unique identifier for a conversation)
     * Multiple messages can belong to the same conversation
     */
    @Column(name = "conversation_id", nullable = false, length = 100)
    private String conversationId;

    /**
     * Message role: USER, ASSISTANT, SYSTEM
     */
    @Column(name = "role", nullable = false, length = 20)
    private String role;

    /**
     * Message content
     */
    @Column(name = "content", nullable = false, length = 5000)
    private String content;

    /**
     * Intent type (optional, for Agent analysis)
     */
    @Column(name = "intent", length = 50)
    private String intent;

    /**
     * Skill identifier (optional, which skill was used)
     */
    @Column(name = "skill", length = 50)
    private String skill;

    /**
     * Token count for this message
     */
    @Column(name = "token_count")
    private Integer tokenCount;

    /**
     * Message creation time
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * Model used for response
     */
    @Column(name = "model", length = 50)
    private String model;
}
