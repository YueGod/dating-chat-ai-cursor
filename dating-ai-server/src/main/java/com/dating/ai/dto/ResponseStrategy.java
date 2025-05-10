package com.dating.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 回复策略
 * 定义AI回复的策略和参数
 *
 * @author dating-ai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResponseStrategy {

    /**
     * 策略类型
     */
    private StrategyType type;

    /**
     * 是否需要话题切换
     */
    private boolean needTopicSwitch;

    /**
     * 目标话题
     */
    private String targetTopic;

    /**
     * 个性化元素列表
     */
    private List<PersonalizationElement> personalizationElements;

    /**
     * 获取适合当前策略的模板列表
     * 
     * @return 模板列表
     */
    public List<String> getSuitableTemplates() {
        switch (type) {
            case HIGH_ENGAGEMENT:
                return Arrays.asList(
                        "我真的很喜欢你说的关于{topic}的看法！{共鸣点}。我曾经也{相关经历}，让我{情感反应}。你平时还会{相关问题}吗？",
                        "哇！这个话题太有意思了！{积极反应}。我之前{相关经历}，感觉超棒的！你有没有{相关问题}啊？真的很好奇！",
                        "这个绝对是我的菜！{具体反应}。说起来，我之前{个人分享}...当时我就在想{思考过程}。你对这个有什么特别的看法吗？");
            case MEDIUM_HIGH_ENGAGEMENT:
                return Arrays.asList(
                        "嗯，我理解你说的关于{topic}的想法，特别是{具体点}这部分。这让我想起了{相关经历}。你平时是怎么看待这个的？",
                        "这个观点很有趣...我自己也{相关经历}，所以能理解你说的。你觉得{开放性问题}？",
                        "我挺喜欢你这个想法的，尤其是{具体点}。我之前{类似经历}时也有类似的感受。你平时会不会{相关问题}？");
            case MEDIUM_ENGAGEMENT:
                return Arrays.asList(
                        "关于{topic}，我觉得{个人观点}...其实最近我也在思考这个问题。你平时会怎么处理这种情况呢？",
                        "这个...让我想想。{思考过程}。不过话说回来，你对{相关话题}有什么看法？",
                        "有意思。我对这个的理解是{看法}，虽然可能不完全对。你经常{相关活动}吗？");
            case LOW_ENGAGEMENT:
                return Arrays.asList(
                        "我明白你的意思。{简短回应}。你有没有想过{转换话题}？",
                        "嗯...{简短回应}。或许我们可以聊聊{新话题}？",
                        "我知道了。要不我们换个轻松点的话题？比如{新话题}？");
            case VERY_LOW_ENGAGEMENT:
                return Arrays.asList(
                        "我理解。{安抚回应}。改天聊？",
                        "嗯，好的。有什么我能帮你的吗？",
                        "明白了。需要休息一下吗？");
            default:
                return Collections.emptyList();
        }
    }

    /**
     * 策略类型枚举
     */
    public enum StrategyType {
        HIGH_ENGAGEMENT, // 高度互动
        MEDIUM_HIGH_ENGAGEMENT, // 中高互动
        MEDIUM_ENGAGEMENT, // 中等互动
        LOW_ENGAGEMENT, // 低互动
        VERY_LOW_ENGAGEMENT // 极低互动
    }

    /**
     * 话题转换风格枚举
     */
    public enum TopicTransitionStyle {
        GRADUAL, // 渐进式转换
        BRIDGING, // 搭桥式转换
        DIRECT // 直接转换
    }

    /**
     * 个性化元素
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PersonalizationElement {
        /**
         * 元素类型
         */
        private ElementType type;

        /**
         * 元素内容
         */
        private String content;

        /**
         * 权重/优先级
         */
        private int weight;

        /**
         * 元素类型枚举
         */
        public enum ElementType {
            PERSONAL_EXPERIENCE, // 个人经历
            OPINION, // 观点
            METAPHOR, // 比喻
            QUESTION, // 问题
            HUMOR, // 幽默
            EMPATHY, // 共情
            POSITIVE_REINFORCEMENT, // 正向强化
            THINKING_PROCESS, // 思考过程
            SELF_CORRECTION, // 自我修正
            MIXED_EMOTION // 混合情绪
        }
    }
}