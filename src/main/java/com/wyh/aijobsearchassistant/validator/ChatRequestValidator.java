package com.wyh.aijobsearchassistant.validator;

import com.wyh.aijobsearchassistant.constant.StatusCode;
import com.wyh.aijobsearchassistant.exception.SystemException;
import org.springframework.stereotype.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Chat Request Validator
 * Validates chat request parameters
 * Uses Java 21 Validation API for enhanced validation
 */
@Component


public class ChatRequestValidator {
    private static final Logger log = LoggerFactory.getLogger(ChatRequestValidator.class);

    private static final int MAX_MESSAGE_LENGTH = 5000;
    private static final int MAX_CONVERSATION_ID_LENGTH = 100;

    /**
     * Validate chat request
     */
    public void validateChatRequest(String message, String conversationId) {
        if (message == null || message.trim().isEmpty()) {
            throw new SystemException(StatusCode.BAD_REQUEST, "Message cannot be empty");
        }

        if (message.length() > MAX_MESSAGE_LENGTH) {
            throw new SystemException(StatusCode.BAD_REQUEST,
                "Message length cannot exceed " + MAX_MESSAGE_LENGTH + " characters");
        }

        if (conversationId != null && conversationId.length() > MAX_CONVERSATION_ID_LENGTH) {
            throw new SystemException(StatusCode.BAD_REQUEST,
                "Conversation ID length cannot exceed " + MAX_CONVERSATION_ID_LENGTH + " characters");
        }

        log.debug("[ChatRequestValidator] Request validation passed");
    }
}
