package com.wyh.aijobsearchassistant.enums;

/**
 * 会话状态枚举
 * 定义会话的生命周期状态
 */
public enum ConversationStatus {
    /**
     * 活跃状态（正常使用中）
     */
    ACTIVE("ACTIVE", "活跃"),

    /**
     * 已删除（软删除标记）
     */
    DELETED("DELETED", "已删除"),

    /**
     * 已归档（用户主动归档）
     */
    ARCHIVED("ARCHIVED", "已归档");

    private final String code;
    private final String description;

    ConversationStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}
