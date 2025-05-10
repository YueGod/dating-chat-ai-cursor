package com.dating.ai.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Chat message entity
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "chat_messages")
@Schema(description = "Chat message")
public class ChatMessage extends BaseEntity {

    /**
     * User ID
     */
    @Schema(description = "User ID")
    private String userId;

    /**
     * Message type: USER or AI
     */
    @Schema(description = "Message type (USER/AI)")
    private MessageType messageType;

    /**
     * Message content
     */
    @Schema(description = "Message content")
    private String content;

    /**
     * Emotional score (for AI analysis)
     */
    @Schema(description = "Emotional score (0-100)")
    private Integer emotionScore;

    /**
     * Conversation ID to group related messages
     */
    @Schema(description = "Conversation ID")
    private String conversationId;

    /**
     * Topic of the message
     */
    @Schema(description = "Message topic")
    private String topic;

    /**
     * Message type enum
     */
    public enum MessageType {
        USER, AI
    }
}