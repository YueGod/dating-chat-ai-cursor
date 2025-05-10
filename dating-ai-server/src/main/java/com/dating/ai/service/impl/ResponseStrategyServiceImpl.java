package com.dating.ai.service.impl;

import com.dating.ai.dto.ConversationContext;
import com.dating.ai.dto.EmotionAnalysisResult;
import com.dating.ai.dto.ResponseStrategy;
import com.dating.ai.dto.ResponseStrategy.PersonalizationElement;
import com.dating.ai.dto.ResponseStrategy.PersonalizationElement.ElementType;
import com.dating.ai.dto.ResponseStrategy.StrategyType;
import com.dating.ai.dto.ResponseStrategy.TopicTransitionStyle;
import com.dating.ai.service.ResponseStrategyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 回复策略服务实现类
 * 根据情绪分析和会话上下文确定回复策略
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ResponseStrategyServiceImpl implements ResponseStrategyService {

    /**
     * 最大话题持续轮数
     */
    @Value("${dating.ai.chat.ai.max-topic-turns:3}")
    private int maxTopicTurns;

    /**
     * 情绪下降阈值
     */
    @Value("${dating.ai.chat.ai.emotion-drop-threshold:15.0}")
    private double emotionDropThreshold;

    /**
     * 话题终结词列表
     */
    private static final List<String> TOPIC_ENDING_TOKENS = Arrays.asList(
            "嗯", "哦", "好的", "好", "行", "知道了", "明白", "是的", "是", "啊", "哈",
            "ok", "OK", "Ok", "噢");

    /**
     * 常见话题列表
     */
    private static final List<String> COMMON_TOPICS = Arrays.asList(
            "旅行", "美食", "音乐", "电影", "书籍", "运动", "爱好", "宠物",
            "工作", "学习", "家庭", "朋友", "未来规划", "梦想", "童年经历");

    @Override
    public ResponseStrategy determineStrategy(EmotionAnalysisResult emotion, ConversationContext context) {
        ResponseStrategy strategy = new ResponseStrategy();

        // 根据情绪分数选择基础策略类型
        if (emotion.getTotalScore() >= 80) {
            strategy.setType(StrategyType.HIGH_ENGAGEMENT);
        } else if (emotion.getTotalScore() >= 60) {
            strategy.setType(StrategyType.MEDIUM_HIGH_ENGAGEMENT);
        } else if (emotion.getTotalScore() >= 40) {
            strategy.setType(StrategyType.MEDIUM_ENGAGEMENT);
        } else if (emotion.getTotalScore() >= 20) {
            strategy.setType(StrategyType.LOW_ENGAGEMENT);
        } else {
            strategy.setType(StrategyType.VERY_LOW_ENGAGEMENT);
        }

        // 确定是否需要话题切换
        boolean needTopicSwitch = determineTopicSwitchNeed(emotion, context);
        strategy.setNeedTopicSwitch(needTopicSwitch);

        // 如果需要切换话题，选择新话题
        if (needTopicSwitch) {
            String newTopic = selectNewTopic(context, emotion.getTotalScore());
            strategy.setTargetTopic(newTopic);
            log.debug("话题切换：从 '{}' 到 '{}'", context.getCurrentTopic(), newTopic);
        }

        // 选择个性化元素
        List<PersonalizationElement> elements = selectPersonalizationElements(emotion, context);
        strategy.setPersonalizationElements(elements);

        log.debug("确定回复策略: 类型={}, 是否切换话题={}, 目标话题={}, 个性化元素数={}",
                strategy.getType(), strategy.isNeedTopicSwitch(),
                strategy.getTargetTopic(), elements.size());

        return strategy;
    }

    @Override
    public boolean determineTopicSwitchNeed(EmotionAnalysisResult emotion, ConversationContext context) {
        // 如果上下文缺失，不建议切换话题
        if (context == null) {
            return false;
        }

        // 当前话题已持续多轮
        boolean longTopic = context.getCurrentTopicTurns() >= maxTopicTurns;

        // 情绪分数显著下降
        boolean emotionDrop = false;
        if (context.getEmotionHistory() != null && !context.getEmotionHistory().isEmpty()) {
            int lastScore = context.getLastEmotionScore();
            int currentScore = emotion.getTotalScore();
            emotionDrop = (lastScore - currentScore) > emotionDropThreshold;
        }

        // 回复长度显著减少
        boolean responseLengthDrop = isResponseLengthSignificantlyReduced(context);

        // 检测到结束词
        boolean endingTokenDetected = containsEndingTokens(context.getLastUserMessage());

        // 综合因素决定是否切换话题
        boolean shouldSwitch = longTopic || emotionDrop || responseLengthDrop || endingTokenDetected;

        if (shouldSwitch) {
            log.debug("建议话题切换: 长话题={}, 情绪下降={}, 回复缩短={}, 结束词={}",
                    longTopic, emotionDrop, responseLengthDrop, endingTokenDetected);
        }

        return shouldSwitch;
    }

    @Override
    public String selectNewTopic(ConversationContext context, int emotionScore) {
        // 如果上下文缺失，选择随机话题
        if (context == null || context.getUserProfile() == null) {
            return selectRandomTopic();
        }

        // 从用户兴趣中选择新话题
        List<String> userInterests = context.getUserProfile().getInterests();

        // 如果用户没有明确的兴趣，使用常见话题
        if (userInterests == null || userInterests.isEmpty()) {
            return selectRandomTopic();
        }

        // 话题切换风格依赖于情绪分数
        TopicTransitionStyle style;
        if (emotionScore >= 60) {
            style = TopicTransitionStyle.GRADUAL;
        } else if (emotionScore >= 40) {
            style = TopicTransitionStyle.BRIDGING;
        } else {
            style = TopicTransitionStyle.DIRECT;
        }

        // 根据风格选择话题
        return selectTopicBasedOnStyle(userInterests, style);
    }

    @Override
    public List<PersonalizationElement> selectPersonalizationElements(
            EmotionAnalysisResult emotion, ConversationContext context) {

        List<PersonalizationElement> elements = new ArrayList<>();
        StrategyType strategyType = getStrategyTypeFromEmotionScore(emotion.getTotalScore());

        // 基于策略类型选择个性化元素
        switch (strategyType) {
            case HIGH_ENGAGEMENT:
                // 高互动：添加个人经历、幽默和积极强化
                addPersonalExperienceElement(elements, context, 90);
                addHumorElement(elements, context, 70);
                addPositiveReinforcementElement(elements, 80);
                addQuestionElement(elements, emotion.getPrimaryIntent(), 90);
                break;

            case MEDIUM_HIGH_ENGAGEMENT:
                // 中高互动：添加观点、共情和问题
                addOpinionElement(elements, context, 80);
                addEmpathyElement(elements, emotion, 70);
                addQuestionElement(elements, emotion.getPrimaryIntent(), 80);
                addThinkingProcessElement(elements, 50);
                break;

            case MEDIUM_ENGAGEMENT:
                // 中等互动：添加思考过程、问题和适当的观点
                addThinkingProcessElement(elements, 80);
                addQuestionElement(elements, emotion.getPrimaryIntent(), 70);
                addOpinionElement(elements, context, 60);
                addSelfCorrectionElement(elements, 40);
                break;

            case LOW_ENGAGEMENT:
                // 低互动：添加简短回应和新话题
                addEmpathyElement(elements, emotion, 80);
                addTopicSuggestionElement(elements, 70);
                addMixedEmotionElement(elements, 30);
                break;

            case VERY_LOW_ENGAGEMENT:
                // 极低互动：添加共情和支持
                addEmpathyElement(elements, emotion, 90);
                addSupportiveElement(elements, 80);
                break;
        }

        // 排序并限制元素数量
        Collections.sort(elements, Comparator.comparing(PersonalizationElement::getWeight).reversed());
        return elements.stream().limit(4).collect(Collectors.toList());
    }

    /**
     * 判断回复长度是否显著减少
     */
    private boolean isResponseLengthSignificantlyReduced(ConversationContext context) {
        if (context == null || context.getRecentMessages() == null || context.getRecentMessages().size() < 3) {
            return false;
        }

        List<ConversationContext.MessagePair> messages = context.getRecentMessages();

        // 获取当前消息长度
        String currentMessage = messages.get(0).getUserMessage();
        int currentLength = currentMessage != null ? currentMessage.length() : 0;

        // 计算之前消息的平均长度
        int previousCount = 0;
        int totalPreviousLength = 0;

        // 最多考虑之前的3条消息
        for (int i = 1; i < Math.min(4, messages.size()); i++) {
            String previousMessage = messages.get(i).getUserMessage();
            if (previousMessage != null) {
                totalPreviousLength += previousMessage.length();
                previousCount++;
            }
        }

        if (previousCount == 0) {
            return false;
        }

        // 计算平均长度
        double averagePreviousLength = (double) totalPreviousLength / previousCount;

        // 如果当前长度小于平均长度的50%，认为显著减少
        return currentLength < averagePreviousLength * 0.5;
    }

    /**
     * 检查消息是否包含话题终结词
     */
    private boolean containsEndingTokens(String message) {
        if (message == null || message.isEmpty()) {
            return false;
        }

        String trimmedMessage = message.trim();

        // 检查消息是否只包含终结词
        for (String token : TOPIC_ENDING_TOKENS) {
            if (trimmedMessage.equals(token)) {
                return true;
            }
        }

        // 如果消息非常短（小于5个字符）且包含终结词
        if (trimmedMessage.length() < 5) {
            for (String token : TOPIC_ENDING_TOKENS) {
                if (trimmedMessage.contains(token)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * 选择随机话题
     */
    private String selectRandomTopic() {
        Random random = new Random();
        return COMMON_TOPICS.get(random.nextInt(COMMON_TOPICS.size()));
    }

    /**
     * 根据风格选择话题
     */
    private String selectTopicBasedOnStyle(List<String> userInterests, TopicTransitionStyle style) {
        Random random = new Random();

        // 如果用户兴趣为空，使用常见话题
        if (userInterests == null || userInterests.isEmpty()) {
            return selectRandomTopic();
        }

        // 从用户兴趣中随机选择
        String selectedTopic = userInterests.get(random.nextInt(userInterests.size()));

        // 根据切换风格添加额外描述
        switch (style) {
            case GRADUAL:
                // 渐进式转换：相关的兴趣方向
                return "与" + selectedTopic + "相关的事情";
            case BRIDGING:
                // 搭桥式转换：你对X有什么看法？
                return selectedTopic;
            case DIRECT:
                // 直接转换：完全不同的话题
                return selectedTopic;
            default:
                return selectedTopic;
        }
    }

    /**
     * 从情绪分数获取策略类型
     */
    private StrategyType getStrategyTypeFromEmotionScore(int score) {
        if (score >= 80)
            return StrategyType.HIGH_ENGAGEMENT;
        if (score >= 60)
            return StrategyType.MEDIUM_HIGH_ENGAGEMENT;
        if (score >= 40)
            return StrategyType.MEDIUM_ENGAGEMENT;
        if (score >= 20)
            return StrategyType.LOW_ENGAGEMENT;
        return StrategyType.VERY_LOW_ENGAGEMENT;
    }

    /**
     * 添加个人经历元素
     */
    private void addPersonalExperienceElement(List<PersonalizationElement> elements, ConversationContext context,
            int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.PERSONAL_EXPERIENCE);
        element.setContent("分享个人经历：与话题相关的假想经历");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加观点元素
     */
    private void addOpinionElement(List<PersonalizationElement> elements, ConversationContext context, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.OPINION);
        element.setContent("表达观点：对话题的看法或理解");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加幽默元素
     */
    private void addHumorElement(List<PersonalizationElement> elements, ConversationContext context, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.HUMOR);
        element.setContent("添加幽默：与话题相关的轻松诙谐表达");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加问题元素
     */
    private void addQuestionElement(List<PersonalizationElement> elements, String intent, int weight) {
        // 如果用户意图本身是提问，降低添加问题的权重
        if ("question".equals(intent)) {
            weight = weight / 2;
        }

        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.QUESTION);
        element.setContent("提问：与主题相关的开放性问题");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加共情元素
     */
    private void addEmpathyElement(List<PersonalizationElement> elements, EmotionAnalysisResult emotion, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.EMPATHY);
        element.setContent("共情表达：理解和认可对方的感受");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加正向强化元素
     */
    private void addPositiveReinforcementElement(List<PersonalizationElement> elements, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.POSITIVE_REINFORCEMENT);
        element.setContent("正向强化：鼓励和认可对方的观点或行为");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加思考过程元素
     */
    private void addThinkingProcessElement(List<PersonalizationElement> elements, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.THINKING_PROCESS);
        element.setContent("思考过程：展示思考和推理过程");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加自我修正元素
     */
    private void addSelfCorrectionElement(List<PersonalizationElement> elements, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.SELF_CORRECTION);
        element.setContent("自我修正：表达想法过程中的自我修正");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加混合情绪元素
     */
    private void addMixedEmotionElement(List<PersonalizationElement> elements, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.MIXED_EMOTION);
        element.setContent("混合情绪：表达复杂或矛盾的情感");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加话题建议元素
     */
    private void addTopicSuggestionElement(List<PersonalizationElement> elements, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.QUESTION);
        element.setContent("话题建议：提出新的话题讨论");
        element.setWeight(weight);
        elements.add(element);
    }

    /**
     * 添加支持性元素
     */
    private void addSupportiveElement(List<PersonalizationElement> elements, int weight) {
        PersonalizationElement element = new PersonalizationElement();
        element.setType(ElementType.EMPATHY);
        element.setContent("支持表达：表示理解和支持");
        element.setWeight(weight);
        elements.add(element);
    }
}