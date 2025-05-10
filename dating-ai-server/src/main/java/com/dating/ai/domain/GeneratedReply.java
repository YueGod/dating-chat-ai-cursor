package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Generated reply entity
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "generated_replies")
public class GeneratedReply extends BaseEntity {

    private String requestId;
    private String userId;
    private String conversationId;
    private String receivedMessageId;
    private List<Reply> replies;
    private GenerationParams generationParams;
    private MessageAnalysis analysis;
    private Performance performance;

    /**
     * Individual reply
     */
    @Data
    public static class Reply {
        private String replyId;
        private String styleId;
        private String styleName;
        private String content;
        private Map<String, Object> metadata;
        private Quality quality;
        private UserFeedback userFeedback;

        /**
         * Quality metrics
         */
        @Data
        public static class Quality {
            private Double relevanceScore;
            private Double styleMatchScore;
            private Double totalScore;
        }

        /**
         * User feedback
         */
        @Data
        public static class UserFeedback {
            private Boolean liked;
            private Boolean copied;
            private Boolean used;
            private String feedback;
            private Date feedbackTime;
        }
    }

    /**
     * Generation parameters
     */
    @Data
    public static class GenerationParams {
        private String model;
        private Double temperature;
        private List<String> styles;
        private Integer contextTurns;
    }

    /**
     * Message analysis
     */
    @Data
    public static class MessageAnalysis {
        private String messageType;
        private String emotion;
        private String interestLevel;
        private List<String> topics;
        private String suggestedResponseTone;
    }

    /**
     * Performance metrics
     */
    @Data
    public static class Performance {
        private Integer requestTime;
        private Integer processingTime;
        private Integer totalTime;
    }
}