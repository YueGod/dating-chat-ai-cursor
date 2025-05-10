package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 订单实体类
 * 记录用户的会员购买订单信息
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "orders")
public class Order extends BaseEntity {

    /**
     * 订单ID
     */
    private String orderId;

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 会员方案ID
     */
    private String planId;

    /**
     * 会员方案名称
     */
    private String planName;

    /**
     * 原始金额
     */
    private BigDecimal amount;

    /**
     * 折扣金额
     */
    private BigDecimal discountAmount;

    /**
     * 最终支付金额
     */
    private BigDecimal finalAmount;

    /**
     * 优惠券代码
     */
    private String couponCode;

    /**
     * 促销活动ID
     */
    private String promotionId;

    /**
     * 支付方式 (wechat, alipay, apple, google)
     */
    private String paymentMethod;

    /**
     * 支付交易ID
     */
    private String paymentTransactionId;

    /**
     * 订单状态 (created, paid, failed, expired, refunded)
     */
    private String orderStatus;

    /**
     * 是否自动续费
     */
    private Boolean autoRenew;

    /**
     * 订单来源
     */
    private String source;

    /**
     * 订单创建时间
     */
    private Date createTime;

    /**
     * 支付时间
     */
    private Date paymentTime;

    /**
     * 过期时间
     */
    private Date expireTime;

    /**
     * 退款时间
     */
    private Date refundTime;

    /**
     * 退款原因
     */
    private String refundReason;

    /**
     * 支付信息
     * 用于存储支付渠道返回的信息
     */
    private PaymentInfo paymentInfo;

    /**
     * 支付信息
     */
    @Data
    public static class PaymentInfo {
        /**
         * 微信支付应用ID
         * 用于标识微信支付的应用
         */
        private String appId;

        /**
         * 时间戳
         * 微信支付接口要求的时间戳参数
         */
        private String timeStamp;

        /**
         * 随机字符串
         * 微信支付接口要求的随机字符串，防重放攻击
         */
        private String nonceStr;

        /**
         * 预支付交易会话标识
         * 微信支付接口返回的预支付ID，格式为"prepay_id=xxx"
         */
        private String packageValue;

        /**
         * 签名类型
         * 微信支付接口的签名算法类型，如MD5、HMAC-SHA256
         */
        private String signType;

        /**
         * 支付签名
         * 微信支付接口的签名字符串，用于前端调起支付
         */
        private String paySign;

        /**
         * 支付渠道原始数据
         * 存储支付渠道返回的完整JSON数据，便于排查问题
         */
        private String rawData;
    }
}