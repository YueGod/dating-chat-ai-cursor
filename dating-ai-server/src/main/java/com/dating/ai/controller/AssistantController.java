package com.dating.ai.controller;

import com.dating.ai.dto.CommonResponse;
import com.dating.ai.dto.GenerateRequestDTO;
import com.dating.ai.service.AssistantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for AI assistant-related endpoints
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/assistant")
@RequiredArgsConstructor
@Tag(name = "Assistant API", description = "AI assistant endpoints")
public class AssistantController {

    private final AssistantService assistantService;

    @PostMapping("/generate")
    @Operation(summary = "Generate replies", description = "Generate AI replies based on a received message")
    public CommonResponse<?> generateReplies(@RequestBody GenerateRequestDTO request) {
        return CommonResponse.success(assistantService.generateReplies(request));
    }

    @PostMapping("/feedback/{replyId}")
    @Operation(summary = "Provide feedback", description = "Provide feedback on a generated reply")
    public CommonResponse<?> provideFeedback(
            @Parameter(description = "Reply ID") @PathVariable String replyId,
            @Parameter(description = "Feedback type") @RequestParam String feedbackType,
            @Parameter(description = "Comment (optional)") @RequestParam(required = false) String comment) {
        return CommonResponse.success(assistantService.provideFeedback(replyId, feedbackType, comment));
    }
}