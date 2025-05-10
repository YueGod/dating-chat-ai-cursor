package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * Data Transfer Object for AI reply generation request
 *
 * @author dating-ai
 */
@Data
@Schema(description = "AI reply generation request")
public class GenerateRequestDTO {

    @Schema(description = "Conversation ID")
    private String conversationId;

    @Schema(description = "Received message content")
    private String receivedMessage;

    @Schema(description = "Style IDs to generate replies with")
    private List<String> styles;

    @Schema(description = "Previous messages for context")
    private List<PreviousMessageDTO> previousMessages;

    @Schema(description = "Context information")
    private ContextDTO context;

    /**
     * Previous message
     */
    @Data
    @Schema(description = "Previous message")
    public static class PreviousMessageDTO {

        @Schema(description = "Message role (received/sent)")
        private String role;

        @Schema(description = "Message content")
        private String content;

        @Schema(description = "Message timestamp")
        private Date timestamp;
    }

    /**
     * Context information
     */
    @Data
    @Schema(description = "Context information")
    public static class ContextDTO {

        @Schema(description = "Relationship stage")
        private String relationshipStage;

        @Schema(description = "Receiver gender")
        private String receiverGender;

        @Schema(description = "Receiver age range")
        private String receiverAgeRange;
    }
}