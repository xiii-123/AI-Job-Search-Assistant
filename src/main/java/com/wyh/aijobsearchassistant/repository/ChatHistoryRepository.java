package com.wyh.aijobsearchassistant.repository;

import com.wyh.aijobsearchassistant.entity.ChatHistory;
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
}
