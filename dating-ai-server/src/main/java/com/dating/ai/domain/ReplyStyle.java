package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Map;

/**
 * Reply style entity for AI-generated responses
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "reply_styles")
public class ReplyStyle extends BaseEntity {

    private String styleId;
    private String name;
    private String description;
    private String longDescription;
    private List<String> tags;
    private Boolean isVipOnly;
    private String category;
    private Double popularity;
    private String iconUrl;
    private Integer displayOrder;
    private Map<String, String> parameters;
    private List<String> patternExamples;
    private List<String> avoidPatterns;
    private String promptModifiers;
    private List<StyleExample> examples;

    /**
     * Style example with received message and reply
     */
    @Data
    public static class StyleExample {
        private String receivedMessage;
        private String replyExample;
    }
}