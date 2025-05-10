package com.dating.ai.service;

import com.dating.ai.dto.GenerateRequestDTO;
import com.dating.ai.dto.GenerateResponseDTO;

import java.util.Map;

/**
 * Service interface for AI assistant operations
 *
 * @author dating-ai
 */
public interface AssistantService {

    /**
     * Generate AI replies based on the provided request
     *
     * @param request Generation request
     * @return Generated replies
     */
    GenerateResponseDTO generateReplies(GenerateRequestDTO request);

    /**
     * Provide feedback on a generated reply
     *
     * @param replyId      Reply ID
     * @param feedbackType Feedback type (like, dislike, copy, used)
     * @param comment      Optional comment
     * @return Feedback result
     */
    Map<String, Object> provideFeedback(String replyId, String feedbackType, String comment);
}