package com.wyh.aijobsearchassistant.repository;

import com.wyh.aijobsearchassistant.entity.ChatHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Chat History Repository Interface
 * JPA repository for chat history data operations
 */
@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Long> {

    /**
     * Find chat history by user ID and conversation ID
     */
    List<ChatHistory> findByUserIdAndConversationIdOrderByCreatedAtAsc(Long userId, String conversationId);

    /**
     * Find all conversations by user ID
     */
    List<ChatHistory> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find distinct conversation IDs by user ID
     */
    List<String> findDistinctConversationIdByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Delete chat history by user ID and conversation ID
     */
    void deleteByUserIdAndConversationId(Long userId, String conversationId);

    /**
     * Find chat history by conversation ID (分页)
     */
    Page<ChatHistory> findByConversationId(String conversationId, Pageable pageable);

    /**
     * Find all messages in a conversation ordered by creation time
     */
    List<ChatHistory> findByConversationIdOrderByCreatedAtAsc(String conversationId);

    /**
     * Count messages in a conversation
     */
    long countByConversationId(String conversationId);
}
