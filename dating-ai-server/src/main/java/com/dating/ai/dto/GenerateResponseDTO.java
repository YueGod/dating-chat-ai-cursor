package com.dating.ai.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * Data Transfer Object for AI reply generation response
 *
 * @author dating-ai
 */
@Data
@Schema(description = "AI reply generation response")
public class GenerateResponseDTO {

    @Schema(description = "Request ID")
    private String requestId;

    @Schema(description = "Generated replies")
    private List<ReplyDTO> replies;

    @Schema(description = "Message analysis")
    private AnalysisDTO analysis;

    @Schema(description = "Generation time in milliseconds")
    private Integer generationTime;

    /**
     * Generated reply
     */
    @Data
    @Schema(description = "Generated reply")
    public static class ReplyDTO {

        @Schema(description = "Reply ID")
        private String replyId;

        @Schema(description = "Style ID")
        private String styleId;

        @Schema(description = "Style name")
        private String styleName;

        @Schema(description = "Reply content")
        private String content;

        @Schema(description = "Reply metadata")
        private Map<String, Object> metadata;

        @Schema(description = "Reply quality metrics")
        private QualityDTO quality;

        /**
         * Reply quality metrics
         */
        @Data
        @Schema(description = "Reply quality metrics")
        public static class QualityDTO {

            @Schema(description = "Relevance score")
            private Double relevanceScore;

            @Schema(description = "Style match score")
            private Double styleMatchScore;

            @Schema(description = "Total score")
            private Double totalScore;
        }
    }

    /**
     * Message analysis
     */
    @Data
    @Schema(description = "Message analysis")
    public static class AnalysisDTO {

        @Schema(description = "Message type")
        private String messageType;

        @Schema(description = "Emotional tone")
        private String emotion;

        @Schema(description = "Interest level")
        private String interestLevel;

        @Schema(description = "Detected topics")
        private List<String> topics;

        @Schema(description = "Suggested response tone")
        private String suggestedResponseTone;
    }
}