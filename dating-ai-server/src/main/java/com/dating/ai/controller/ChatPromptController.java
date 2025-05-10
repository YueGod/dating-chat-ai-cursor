package com.dating.ai.controller;

import com.dating.ai.service.impl.AIServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 提示词模板聊天控制器
 * 提供基于不同提示词模板的聊天API
 *
 * @author dating-ai
 */
@RestController
@RequestMapping("/api/chat/prompt")
@Tag(name = "提示词模板聊天", description = "基于不同提示词模板的聊天API")
@RequiredArgsConstructor
@Slf4j
public class ChatPromptController {

        private final AIServiceImpl aiService;

        /**
         * 使用综合响应模板的聊天API
         *
         * @param userId       用户ID
         * @param input        用户输入
         * @param emotionScore 用户情绪评分（可选）
         * @return 聊天回复
         */
        @GetMapping("/chat")
        @Operation(summary = "基础聊天", description = "使用综合响应模板的聊天API")
        public Map<String, Object> chatWithResponse(
                        @RequestParam(defaultValue = "user123") String userId,
                        @RequestParam String input,
                        @RequestParam(required = false) Integer emotionScore) {

                log.info("综合模板聊天请求: userId={}, input={}, emotionScore={}", userId, input, emotionScore);

                Map<String, String> parameters = new HashMap<>();

                // 如果提供了情绪评分，加入参数
                if (emotionScore != null) {
                        parameters.put("emotion_score", emotionScore.toString());
                }

                String response = aiService.generateChatReplyWithTemplate(
                                input,
                                userId,
                                "consolidated-response",
                                parameters);

                return Map.of(
                                "userId", userId,
                                "input", input,
                                "response", response);
        }

        /**
         * 使用分析模板进行用户输入分析
         *
         * @param userId 用户ID
         * @param input  用户输入
         * @return 分析结果
         */
        @GetMapping("/analyze")
        @Operation(summary = "情绪分析", description = "分析用户输入的情绪和意图")
        public Map<String, Object> analyzeUserInput(
                        @RequestParam(defaultValue = "user123") String userId,
                        @RequestParam String input) {

                log.info("用户输入分析请求: userId={}, input={}", userId, input);

                Map<String, String> parameters = new HashMap<>();
                parameters.put("user_input", input);
                parameters.put("last_response_time", String.valueOf(System.currentTimeMillis() - 60000)); // 模拟1分钟前
                parameters.put("current_time", String.valueOf(System.currentTimeMillis()));

                // 添加对话历史参数（可以从实际存储中获取）
                parameters.put("conversation_history", "这里是历史对话内容");

                // 加入默认主题信息
                parameters.put("current_topic", "约会建议");
                parameters.put("topic_turns", "1");
                parameters.put("topic_interest", "7");
                parameters.put("primary_intent", "寻求建议");
                parameters.put("secondary_intent", "分享经历");
                parameters.put("self_disclosure_level", "5");
                parameters.put("connection_strength", "4");
                parameters.put("trust_level", "3");

                String response = aiService.generateChatReplyWithTemplate(
                                input,
                                userId,
                                "consolidated-analysis",
                                parameters);

                return Map.of(
                                "userId", userId,
                                "input", input,
                                "analysis", response);
        }

        /**
         * 使用个性化参数的聊天API
         *
         * @param userId            用户ID
         * @param input             用户输入
         * @param personalityStyle  性格风格
         * @param emotionExpression 情感表达方式
         * @return 聊天回复
         */
        @GetMapping("/personality")
        @Operation(summary = "个性化聊天", description = "使用可定制个性的提示词模板的聊天API")
        public Map<String, Object> personalityChat(
                        @RequestParam(defaultValue = "user123") String userId,
                        @RequestParam String input,
                        @RequestParam(defaultValue = "友善开朗") String personalityStyle,
                        @RequestParam(defaultValue = "温暖共情") String emotionExpression) {

                log.info("个性化聊天请求: userId={}, personalityStyle={}, emotionExpression={}",
                                userId, personalityStyle, emotionExpression);

                Map<String, String> parameters = new HashMap<>();
                parameters.put("personality_style", personalityStyle);
                parameters.put("emotional_traits", emotionExpression);
                parameters.put("communication_style", "亲切自然");
                parameters.put("thinking_patterns", "平衡理性与直觉");
                parameters.put("typical_opening_phrase", "让我思考一下这个问题...");
                parameters.put("thinking_process_phrase", "从我的角度来看...");
                parameters.put("advice_giving_phrase", "基于我的经验，建议你...");
                parameters.put("empathy_expression", "我能理解你的感受...");
                parameters.put("topic_transition_phrase", "说到这个，我想到了...");
                parameters.put("hesitation_phrase", "嗯...这个问题有点复杂...");
                parameters.put("correction_phrase", "等等，我可能没表达清楚...");

                String response = aiService.generateChatReplyWithTemplate(
                                input,
                                userId,
                                "consolidated-response",
                                parameters);

                return Map.of(
                                "userId", userId,
                                "input", input,
                                "personalityStyle", personalityStyle,
                                "emotionExpression", emotionExpression,
                                "response", response);
        }

        /**
         * 基于RAG上下文的聊天API
         *
         * @param userId 用户ID
         * @param input  用户输入
         * @return 聊天回复
         */
        @GetMapping("/rag")
        @Operation(summary = "RAG聊天", description = "基于检索增强生成(RAG)的聊天API")
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