package com.dating.ai.controller;

import com.dating.ai.domain.ChatMessage;
import com.dating.ai.dto.ChatRequestDTO;
import com.dating.ai.dto.ChatResponseDTO;
import com.dating.ai.dto.CommonResponse;
import com.dating.ai.service.ChatBotService;
import com.dating.ai.service.ChatService;
import com.dating.ai.service.AIService;
import com.dating.ai.service.impl.AIServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 聊天控制器
 * 提供聊天相关的API接口
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "聊天API", description = "提供聊天、语义搜索等功能")
public class ChatController {

    private final ChatService chatService;
    private final ChatBotService chatBotService;
    private final AIServiceImpl aiService;

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
        log.debug("接收到聊天请求: {}", chatRequest);
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
        log.debug("接收到搜索请求 - 查询: {}, 会话: {}, 限制: {}", query, conversationId, limit);
        List<Map<String, Object>> results = chatBotService.searchRelatedMessages(query, conversationId, limit);
        return CommonResponse.success(results);
    }

    @GetMapping("/history/{userId}")
    @Operation(summary = "Get chat history", description = "Retrieve chat history for a user")
    public CommonResponse<Page<ChatMessage>> getChatHistory(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Conversation ID (optional)") @RequestParam(required = false) String conversationId,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size) {
        Page<ChatMessage> history = chatService.getChatHistory(userId, conversationId,
                PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime")));
        return CommonResponse.success(history);
    }

    @DeleteMapping("/conversation/{userId}/{conversationId}")
    @Operation(summary = "Delete a conversation", description = "Delete all messages in a conversation")
    public CommonResponse<Void> deleteConversation(
            @Parameter(description = "User ID") @PathVariable String userId,
            @Parameter(description = "Conversation ID") @PathVariable String conversationId) {
        chatService.deleteConversation(userId, conversationId);
        return CommonResponse.success();
    }

    @PostMapping("/conversation/{userId}")
    @Operation(summary = "Start a new conversation", description = "Create a new conversation for a user")
    public CommonResponse<String> startNewConversation(
            @Parameter(description = "User ID") @PathVariable String userId) {
        String conversationId = chatService.startNewConversation(userId);
        return CommonResponse.success(conversationId);
    }

    /**
     * 使用基础提示词模板的聊天API
     *
     * @param userId 用户ID
     * @param input  用户输入
     * @return 聊天回复
     */
    @GetMapping("/basic")
    public Map<String, Object> basicChat(
            @RequestParam(defaultValue = "user123") String userId,
            @RequestParam String input) {

        log.info("基础聊天请求: userId={}, input={}", userId, input);

        String response = aiService.generateChatReplyWithTemplate(
                input,
                userId,
                "base-assistant",
                new HashMap<>());

        return Map.of(
                "userId", userId,
                "input", input,
                "response", response);
    }

    /**
     * 使用情感支持模板的聊天API
     *
     * @param userId 用户ID
     * @param input  用户输入
     * @return 聊天回复
     */
    @GetMapping("/emotional")
    public Map<String, Object> emotionalChat(
            @RequestParam(defaultValue = "user123") String userId,
            @RequestParam String input) {

        log.info("情感支持聊天请求: userId={}, input={}", userId, input);

        String response = aiService.generateChatReplyWithTemplate(
                input,
                userId,
                "emotional-support",
                new HashMap<>());

        return Map.of(
                "userId", userId,
                "input", input,
                "response", response);
    }

    /**
     * 使用约会建议模板的聊天API
     *
     * @param userId 用户ID
     * @param input  用户输入
     * @return 聊天回复
     */
    @GetMapping("/advice")
    public Map<String, Object> adviceChat(
            @RequestParam(defaultValue = "user123") String userId,
            @RequestParam String input) {

        log.info("约会建议聊天请求: userId={}, input={}", userId, input);

        String response = aiService.generateChatReplyWithTemplate(
                input,
                userId,
                "dating-advice",
                new HashMap<>());

        return Map.of(
                "userId", userId,
                "input", input,
                "response", response);
    }

    /**
     * 使用个性化模板的聊天API
     *
     * @param userId             用户ID
     * @param input              用户输入
     * @param name               助手名称
     * @param personalityStyle   性格风格
     * @param communicationStyle 交流风格
     * @return 聊天回复
     */
    @GetMapping("/personality")
    public Map<String, Object> personalityChat(
            @RequestParam(defaultValue = "user123") String userId,
            @RequestParam String input,
            @RequestParam(defaultValue = "Alex") String name,
            @RequestParam(defaultValue = "友善开朗") String personalityStyle,
            @RequestParam(defaultValue = "亲切幽默") String communicationStyle) {

        log.info("个性化聊天请求: userId={}, name={}, personalityStyle={}",
                userId, name, personalityStyle);

        Map<String, String> parameters = new HashMap<>();
        parameters.put("name", name);
        parameters.put("personality_style", personalityStyle);
        parameters.put("communication_style", communicationStyle);
        parameters.put("professional_background", "心理学和人际关系学");
        parameters.put("interests", "心理学、电影和户外活动");
        parameters.put("values", "真诚、尊重和理解");
        parameters.put("typical_opening_phrase", "让我思考一下这个问题...");
        parameters.put("thinking_process_phrase", "从心理学角度来看...");
        parameters.put("advice_giving_phrase", "基于我的经验，建议你...");
        parameters.put("empathy_expression", "我能理解你的感受...");

        String response = aiService.generateChatReplyWithTemplate(
                input,
                userId,
                "personality-template",
                parameters);

        return Map.of(
                "userId", userId,
                "input", input,
                "assistantName", name,
                "personalityStyle", personalityStyle,
                "response", response);
    }

    /**
     * 基于模拟上下文的RAG聊天API
     * 在实际应用中，上下文应从向量数据库检索
     *
     * @param userId 用户ID
     * @param input  用户输入
     * @return 聊天回复
     */
    @GetMapping("/rag")
    public Map<String, Object> ragChat(
            @RequestParam(defaultValue = "user123") String userId,
            @RequestParam String input) {

        log.info("RAG聊天请求: userId={}, input={}", userId, input);

        // 模拟从向量数据库检索的上下文
        String mockContext = """
                约会初期常见错误：
                1. 过度分享个人信息：在初次约会时分享过多私人细节可能会让对方感到不舒适。建议保持适度的自我披露。
                2. 过于关注自己：持续谈论自己而不倾听对方是常见错误。成功的约会应该是双向交流。
                3. 过早讨论未来：在初期约会就谈论长期计划可能吓跑对方。循序渐进地发展关系更为明智。
                4. 纠结于前任：讨论前任关系会给新的约会蒙上阴影。专注于当下和未来更为健康。

                有效约会沟通技巧：
                1. 积极倾听：真正专注于对方所说的话，而不只是等待自己说话的机会。
                2. 开放式提问：使用"如何"、"为什么"和"什么"等词开始的问题，鼓励更深入的对话。
                3. 身体语言意识：保持适当的眼神接触，展示开放的姿态，这些都能传达兴趣和吸引力。
                4. 真诚赞美：特定而真诚的赞美比泛泛的恭维更有效，显示你真正注意到了对方的特质。

                处理约会焦虑的方法：
                1. 正念呼吸：深呼吸可以平静神经系统，缓解约会前的紧张情绪。
                2. 重新框定思维：将约会视为了解新朋友的机会，而非评判或被评判的场合。
                3. 准备话题：提前准备几个谈话话题可以减轻担忧，避免尴尬的沉默。
                4. 设定现实期望：记住每次约会都是一次经历，无论结果如何都有价值。
                """;

        String response = aiService.generateChatReplyWithContext(input, userId, mockContext);

        return Map.of(
                "userId", userId,
                "input", input,
                "response", response,
                "contextUsed", true);
    }
}