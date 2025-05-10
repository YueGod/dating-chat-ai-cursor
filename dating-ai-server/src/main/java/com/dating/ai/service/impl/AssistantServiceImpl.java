package com.dating.ai.service.impl;

import com.dating.ai.dao.GeneratedReplyRepository;
import com.dating.ai.dao.ReplyStyleRepository;
import com.dating.ai.domain.GeneratedReply;
import com.dating.ai.domain.ReplyStyle;
import com.dating.ai.dto.GenerateRequestDTO;
import com.dating.ai.dto.GenerateResponseDTO;
import com.dating.ai.service.AssistantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Implementation of the AssistantService interface
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AssistantServiceImpl implements AssistantService {

    private final GeneratedReplyRepository generatedReplyRepository;
    private final ReplyStyleRepository styleRepository;

    // TODO: Replace with actual user context mechanism
    private static final String CURRENT_USER_ID = "user123456";

    @Override
    public GenerateResponseDTO generateReplies(GenerateRequestDTO request) {
        long startTime = System.currentTimeMillis();

        GenerateResponseDTO response = new GenerateResponseDTO();
        response.setRequestId(UUID.randomUUID().toString());

        // Create simple mock replies
        List<GenerateResponseDTO.ReplyDTO> replies = new ArrayList<>();

        for (String styleId : request.getStyles()) {
            ReplyStyle style = styleRepository.findByStyleId(styleId)
                    .orElseThrow(() -> new RuntimeException("Style not found: " + styleId));

            GenerateResponseDTO.ReplyDTO reply = new GenerateResponseDTO.ReplyDTO();
            reply.setReplyId(UUID.randomUUID().toString());
            reply.setStyleId(style.getStyleId());
            reply.setStyleName(style.getName());

            // Generate simple response based on style
            reply.setContent(generateSimpleReply(request.getReceivedMessage(), style));

            // Add metadata
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("wordCount", reply.getContent().split("\\s+").length);
            metadata.put("emotionalIndex", ThreadLocalRandom.current().nextInt(60, 95));
            reply.setMetadata(metadata);

            // Add quality scores
            GenerateResponseDTO.ReplyDTO.QualityDTO quality = new GenerateResponseDTO.ReplyDTO.QualityDTO();
            quality.setRelevanceScore(0.90);
            quality.setStyleMatchScore(0.85);
            quality.setTotalScore(0.88);
            reply.setQuality(quality);

            replies.add(reply);
        }

        response.setReplies(replies);

        // Add message analysis
        GenerateResponseDTO.AnalysisDTO analysis = new GenerateResponseDTO.AnalysisDTO();
        analysis.setMessageType(request.getReceivedMessage().contains("?") ? "question" : "statement");
        analysis.setEmotion("neutral");
        analysis.setInterestLevel("medium");
        analysis.setTopics(Collections.singletonList("general"));
        analysis.setSuggestedResponseTone("friendly");

        response.setAnalysis(analysis);

        // Record generation time
        long endTime = System.currentTimeMillis();
        response.setGenerationTime((int) (endTime - startTime));

        return response;
    }

    @Override
    public Map<String, Object> provideFeedback(String replyId, String feedbackType, String comment) {
        log.info("Feedback received for reply {}: {}", replyId, feedbackType);

        // In a real implementation, we would save this feedback to the database

        // Return simple response
        Map<String, Object> result = new HashMap<>();
        result.put("replyId", replyId);
        result.put("feedbackSaved", true);

        return result;
    }

    /**
     * Generate simple reply based on received message and style
     *
     * @param receivedMessage Received message
     * @param style           Reply style
     * @return Generated content
     */
    private String generateSimpleReply(String receivedMessage, ReplyStyle style) {
        // Default responses based on style
        switch (style.getStyleId()) {
            case "casual":
                return "嗯，这个想法不错！我们可以找个时间聊聊具体细节。你有什么特别想法吗？";
            case "humorous":
                return "哈哈，这个提议太棒了！正好我最近缺乏一些冒险，要不咱们来点不一样的？😜 说说你的想法？";
            case "romantic":
                return "看到你的消息，突然觉得这一天变得更美好了✨ 这个主意很棒，很期待能和你一起。";
            default:
                return "谢谢你的消息！这听起来很有趣，我很期待。我们可以再详细聊聊具体的计划。";
        }
    }
}