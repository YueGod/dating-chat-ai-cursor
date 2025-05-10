package com.dating.ai.controller;

import com.dating.ai.service.AIService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * AI测试控制器
 * 用于测试AI服务的基本功能
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/ai/test")
@RequiredArgsConstructor
@Slf4j
public class AITestController {

    private final AIService aiService;

    /**
     * 测试AI聊天功能
     *
     * @param input 用户输入
     * @return AI回复
     */
    @GetMapping("/chat")
    public Map<String, String> testChat(@RequestParam String input) {
        log.info("测试AI聊天: {}", input);
        String response = aiService.generateChatReply(input, "test-conversation",
                "你是一个专业的约会顾问助手，帮助用户解决约会和社交中的问题。");
        return Map.of(
                "input", input,
                "response", response);
    }

    /**
     * 测试AI嵌入功能
     *
     * @param text 需要嵌入的文本
     * @return 嵌入向量的前5个数字和向量维度
     */
    @GetMapping("/embedding")
    public Map<String, Object> testEmbedding(@RequestParam String text) {
        log.info("测试AI嵌入: {}", text);
        List<Float> embedding = aiService.generateEmbedding(text);

        // 只返回前5个数字和维度，避免返回过大的响应
        List<Float> preview = embedding.subList(0, Math.min(5, embedding.size()));

        return Map.of(
                "text", text,
                "dimension", embedding.size(),
                "preview", preview);
    }
}