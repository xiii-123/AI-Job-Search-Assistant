package com.wyh.aijobsearchassistant.enums;

/**
 * 消息角色枚举
 * 定义聊天消息的角色类型
 */
public enum MessageRole {
    /**
     * 用户消息
     */
    USER("ROLE_USER", "用户"),

    /**
     * AI助手消息
     */
    ASSISTANT("ROLE_ASSISTANT", "助手"),

    /**
     * 系统消息
     */
    SYSTEM("ROLE_SYSTEM", "系统");

    private final String code;
    private final String description;

    MessageRole(String code, String description) {
        this.code = code;
        this.description = description;
    }

    public String getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据数据库中的 role 字符串值转换为枚举
     */
    public static MessageRole fromString(String role) {
        if (role == null) {
            return null;
        }
        for (MessageRole mr : MessageRole.values()) {
            if (mr.name().equalsIgnoreCase(role)) {
                return mr;
            }
        }
        return null;
    }

    /**
     * 获取用于数据库存储的字符串值（直接返回枚举名称）
     */
    public String toDbValue() {
        return this.name();
    }
}
