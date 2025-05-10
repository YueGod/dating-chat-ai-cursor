package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 用户会员信息实体类
 * 记录用户的会员状态、订阅信息和有效期
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "user_memberships")
public class UserMembership extends BaseEntity {

    /**
     * 用户ID
     * 会员所属用户的唯一标识
     */
    private String userId;

    /**
     * 会员方案ID
     * 用户订阅的会员方案标识
     */
    private String planId;

    /**
     * 会员方案名称
     * 用户订阅的会员方案名称
     */
    private String planName;

    /**
     * 开始时间
     * 会员生效的开始时间
     */
    private Date startTime;

    /**
     * 到期时间
     * 会员的到期时间，超过此时间会员将失效
     */
    private Date expireTime;

    /**
     * 是否有效
     * 标识该会员记录是否处于有效状态
     */
    private Boolean isActive;

    /**
     * 是否自动续费
     * 标识是否在会员到期后自动续费
     */
    private Boolean autoRenew;

    /**
     * 原始订单ID
     * 购买此会员的订单ID，用于跟踪订单来源
     */
    private String originalOrderId;

    /**
     * 下次扣费日期
     * 如果开启自动续费，记录下次扣费的时间
     */
    private Date nextBillingDate;

    /**
     * 下次扣费金额
     * 如果开启自动续费，记录下次扣费的金额
     */
    private BigDecimal nextBillingAmount;

    /**
     * 取消原因
     * 用户取消自动续费的原因，用于数据分析
     */
    private String cancelReason;
}