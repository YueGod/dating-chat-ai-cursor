package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

/**
 * Conversation entity
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "conversations")
public class Conversation extends BaseEntity {

    private String conversationId;
    private String userId;
    private ContactInfo contactInfo;
    private List<String> tags;
    private Boolean isStarred;
    private LastMessage lastMessage;
    private Integer messageCount;
    private Integer unreadCount;
    private String status;
    private Date deletedAt;

    /**
     * Contact information
     */
    @Data
    public static class ContactInfo {
        private String name;
        private String avatar;
        private String relationship;
        private String platform;
        private String externalId;
    }

    /**
     * Last message in the conversation
     */
    @Data
    public static class LastMessage {
        private String messageId;
        private String content;
        private String type;
        private Date timestamp;
    }
}