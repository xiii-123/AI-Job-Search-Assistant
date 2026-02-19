package com.wyh.aijobsearchassistant.repository;

import com.wyh.aijobsearchassistant.entity.Conversation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Conversation Repository
 * 会话数据访问层
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

    /**
     * 根据 conversationId 查询会话
     */
    Optional<Conversation> findByConversationId(String conversationId);

    /**
     * 根据 conversationId 和 userId 查询会话（验证归属权）
     */
    Optional<Conversation> findByConversationIdAndUserId(String conversationId, Long userId);

    /**
     * 查询用户的所有会话（分页、按更新时间倒序）
     */
    Page<Conversation> findByUserIdAndStatusOrderByUpdatedAtDesc(Long userId, String status, Pageable pageable);

    /**
     * 查询用户的会话，支持按名称模糊搜索（分页）
     */
    Page<Conversation> findByUserIdAndStatusAndNameContainingOrderByUpdatedAtDesc(
            Long userId, String status, String keyword, Pageable pageable);

    /**
     * 统计用户的活跃会话数
     */
    long countByUserIdAndStatus(Long userId, String status);

    /**
     * 检查会话是否存在并属于指定用户
     */
    boolean existsByConversationIdAndUserId(String conversationId, Long userId);

    /**
     * 删除用户的所有会话（级联删除会在 Service 层处理）
     */
    void deleteByUserId(Long userId);

    /**
     * 查询会话的消息数量（使用子查询）
     */
    @Query("SELECT c FROM Conversation c WHERE c.userId = :userId AND c.status = :status " +
           "AND (:keyword IS NULL OR c.name LIKE %:keyword%) " +
           "ORDER BY c.updatedAt DESC")
    Page<Conversation> findByUserIdAndStatusWithKeyword(
            @Param("userId") Long userId,
            @Param("status") String status,
            @Param("keyword") String keyword,
            Pageable pageable
    );
}
