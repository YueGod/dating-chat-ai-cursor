package com.dating.ai.constant;

/**
 * 错误码常量类
 * 统一管理系统所有错误码
 * <p>
 * 错误码规则：
 * 1. 格式：6位数字，前3位代表模块，后3位代表错误编号
 * 2. 模块编号:
 * - 000: 通用错误
 * - 001: 用户模块
 * - 002: 认证模块
 * - 003: 会员模块
 * - 004: 订单模块
 * - 005: 支付模块
 * - 006: 聊天模块
 * 3. 错误编号:
 * - 4xx: 客户端错误 (如400参数错误，404资源不存在)
 * - 5xx: 服务端错误 (如500内部错误)
 *
 * @author dating-ai
 */
public class ErrorCode {

    /**
     * 通用错误码
     */
    public static class Common {
        /** 成功 */
        public static final String SUCCESS = "000000";
        /** 未知错误 */
        public static final String UNKNOWN_ERROR = "000500";
        /** 系统内部错误 */
        public static final String INTERNAL_ERROR = "000500";
        /** 参数错误 */
        public static final String PARAM_ERROR = "000400";
        /** 未授权 */
        public static final String UNAUTHORIZED = "000401";
        /** 权限不足 */
        public static final String FORBIDDEN = "000403";
        /** 资源不存在 */
        public static final String NOT_FOUND = "000404";
        /** 请求超时 */
        public static final String TIMEOUT = "000408";
        /** 请求过于频繁 */
        public static final String TOO_MANY_REQUESTS = "000429";
    }

    /**
     * 用户模块错误码
     */
    public static class User {
        /** 用户不存在 */
        public static final String USER_NOT_FOUND = "001404";
        /** 用户已存在 */
        public static final String USER_ALREADY_EXISTS = "001409";
        /** 用户被禁用 */
        public static final String USER_DISABLED = "001403";
        /** 用户设置保存失败 */
        public static final String SETTINGS_SAVE_FAILED = "001500";
    }

    /**
     * 认证模块错误码
     */
    public static class Auth {
        /** 登录失败 */
        public static final String LOGIN_FAILED = "002401";
        /** 令牌无效 */
        public static final String INVALID_TOKEN = "002401";
        /** 令牌已过期 */
        public static final String TOKEN_EXPIRED = "002401";
        /** 刷新令牌无效 */
        public static final String INVALID_REFRESH_TOKEN = "002401";
    }

    /**
     * 会员模块错误码
     */
    public static class Membership {
        /** 会员不存在 */
        public static final String MEMBERSHIP_NOT_FOUND = "003404";
        /** 会员已过期 */
        public static final String MEMBERSHIP_EXPIRED = "003400";
        /** 会员已设置为不自动续费 */
        public static final String ALREADY_CANCELED_RENEWAL = "003400";
        /** 会员方案不存在 */
        public static final String PLAN_NOT_FOUND = "003404";
        /** 不支持的时长单位 */
        public static final String UNSUPPORTED_DURATION_TYPE = "003500";
        /** 无权访问VIP内容 */
        public static final String VIP_ACCESS_DENIED = "003403";
    }

    /**
     * 订单模块错误码
     */
    public static class Order {
        /** 订单不存在 */
        public static final String ORDER_NOT_FOUND = "004404";
        /** 订单已支付 */
        public static final String ORDER_ALREADY_PAID = "004400";
        /** 订单未支付 */
        public static final String ORDER_NOT_PAID = "004400";
        /** 订单已过期 */
        public static final String ORDER_EXPIRED = "004400";
        /** 订单创建失败 */
        public static final String ORDER_CREATE_FAILED = "004500";
    }

    /**
     * 支付模块错误码
     */
    public static class Payment {
        /** 支付失败 */
        public static final String PAYMENT_FAILED = "005500";
        /** 退款失败 */
        public static final String REFUND_FAILED = "005500";
        /** 不支持的支付方式 */
        public static final String UNSUPPORTED_PAYMENT_METHOD = "005400";
        /** 支付金额不正确 */
        public static final String INCORRECT_AMOUNT = "005400";
    }

    /**
     * 聊天模块错误码
     */
    public static class Chat {
        /** 消息发送失败 */
        public static final String MESSAGE_SEND_FAILED = "006500";
        /** AI生成回复失败 */
        public static final String AI_GENERATE_FAILED = "006500";
        /** 超出聊天限制 */
        public static final String CHAT_LIMIT_EXCEEDED = "006403";
        /** 会话不存在 */
        public static final String CONVERSATION_NOT_FOUND = "006404";
        /** 内容违规 */
        public static final String CONTENT_VIOLATION = "006400";
    }
}