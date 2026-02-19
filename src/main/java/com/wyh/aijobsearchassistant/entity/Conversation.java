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
 * Conversation Entity
 * 会话实体，管理用户的对话会话
 * 每个会话包含多条聊天记录（ChatHistory）
 */
@Entity
@Table(name = "conversation", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_created_at", columnList = "createdAt"),
    @Index(name = "idx_updated_at", columnList = "updatedAt")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 会话唯一标识（UUID）
     * 与 ChatHistory 中的 conversationId 关联
     */
    @Column(name = "conversation_id", nullable = false, unique = true, length = 100)
    private String conversationId;

    /**
     * 所属用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 会话名称（用户自定义或从第一条消息提取）
     */
    @Column(name = "name", length = 200)
    private String name;

    /**
     * 会话状态：ACTIVE, DELETED, ARCHIVED
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ACTIVE";

    /**
     * 该会话下的消息总数
     */
    @Column(name = "message_count")
    @Builder.Default
    private Integer messageCount = 0;

    /**
     * 会话创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime createdAt;

    /**
     * 最后更新时间（最后一条消息的时间）
     */
    @Column(name = "updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    /**
     * 检查会话是否活跃
     */
    public boolean isActive() {
        return "ACTIVE".equals(status);
    }

    /**
     * 检查会话是否已删除
     */
    public boolean isDeleted() {
        return "DELETED".equals(status);
    }
}
