package com.dating.ai.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

/**
 * 用户资料DTO
 * 存储用户基本资料和偏好信息
 *
 * @author dating-ai
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 性别
     */
    private String gender;

    /**
     * 年龄
     */
    private Integer age;

    /**
     * 兴趣爱好列表
     */
    private List<String> interests;

    /**
     * 话题偏好（话题及其兴趣度）
     */
    private Map<String, Integer> topicPreferences;

    /**
     * 偏好人格类型
     */
    private PersonaType preferredPersona;

    /**
     * 用户平均回复长度
     */
    private int avgResponseLength;

    /**
     * 用户平均情绪分数
     */
    private int avgEmotionScore;

    /**
     * 是否VIP用户
     */
    private boolean isVip;

    /**
     * 隐私设置
     */
    private PrivacySettings privacySettings;

    /**
     * 会员等级
     */
    private MembershipLevel membershipLevel;

    /**
     * 人格类型枚举
     */
    public enum PersonaType {
        ROMANTIC, // 浪漫型
        HUMOROUS, // 幽默型
        MYSTERIOUS, // 神秘型
        INTELLECTUAL, // 知性型
        CARING, // 关怀型
        ADVENTUROUS, // 冒险型
        CONFIDENT, // 自信型
        CASUAL // 随性型
    }

    /**
     * 会员等级枚举
     */
    public enum MembershipLevel {
        FREE, // 免费用户
        VIP, // VIP会员
        PREMIUM // 高级会员
    }

    /**
     * 隐私设置
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PrivacySettings {
        /**
         * 隐私级别
         */
        private PrivacyLevel privacyLevel;

        /**
         * 聊天历史保留天数
         */
        private int chatHistoryRetention;

        /**
         * 是否启用高隐私模式
         */
        private boolean isHighPrivacy;

        /**
         * 隐私级别枚举
         */
        public enum PrivacyLevel {
            STANDARD, // 标准级别
            STRICT // 严格级别
        }
    }
}