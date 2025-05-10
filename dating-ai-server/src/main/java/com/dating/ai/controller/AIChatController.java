package com.dating.ai.controller;

import com.dating.ai.dto.ChatRequestDTO;
import com.dating.ai.dto.ChatResponseDTO;
import com.dating.ai.dto.CommonResponse;
import com.dating.ai.service.ChatBotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI聊天控制器
 * 提供AI聊天和语义搜索相关的API接口
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/api/ai-chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "AI聊天API", description = "提供AI聊天、语义搜索等功能")
public class AIChatController {

    private final ChatBotService chatBotService;

    /**
     * 发送聊天消息
     * 处理用户发送的消息并返回AI回复
     *
     * @param chatRequest 聊天请求
     * @return 聊天响应
     */
    @PostMapping("/send")
    @Operation(summary = "发送消息", description = "发送聊天消息并获取AI回复")
    public CommonResponse<ChatResponseDTO> sendMessage(@RequestBody ChatRequestDTO chatRequest) {
        log.debug("接收到AI聊天请求: {}", chatRequest);
        ChatResponseDTO response = chatBotService.chat(chatRequest);
        return CommonResponse.success(response);
    }

    /**
     * 语义搜索相关消息
     * 基于向量相似度搜索相关消息
     *
     * @param query          查询文本
     * @param conversationId 会话ID（可选）
     * @param limit          结果限制（默认10）
     * @return 相关消息列表
     */
    @GetMapping("/search")
    @Operation(summary = "语义搜索", description = "搜索与查询文本语义相关的消息")
    public CommonResponse<List<Map<String, Object>>> searchMessages(
            @Parameter(description = "查询文本") @RequestParam String query,
            @Parameter(description = "会话ID（可选）") @RequestParam(required = false) String conversationId,
            @Parameter(description = "结果数量限制") @RequestParam(defaultValue = "10") int limit) {
        log.debug("接收到语义搜索请求 - 查询: {}, 会话: {}, 限制: {}", query, conversationId, limit);
        List<Map<String, Object>> results = chatBotService.searchRelatedMessages(query, conversationId, limit);
        return CommonResponse.success(results);
    }
}