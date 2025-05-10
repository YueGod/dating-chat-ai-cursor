package com.dating.ai.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.mongodb.core.mapping.Document;

import java.math.BigDecimal;
import java.util.List;

/**
 * 会员方案实体类
 * 定义可购买的各种会员套餐
 *
 * @author dating-ai
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Document(collection = "membership_plans")
public class MembershipPlan extends BaseEntity {

    /**
     * 方案ID
     * 唯一标识会员方案的ID
     */
    private String planId;

    /**
     * 方案名称
     * 会员套餐的显示名称，例如"月度会员"、"年度会员"等
     */
    private String planName;

    /**
     * 方案描述
     * 会员套餐的详细描述信息，介绍会员权益等
     */
    private String planDescription;

    /**
     * 价格
     * 会员套餐的实际销售价格
     */
    private BigDecimal price;

    /**
     * 原价
     * 会员套餐的原始价格，用于显示折扣力度
     */
    private BigDecimal originalPrice;

    /**
     * 时长
     * 会员有效期的数值，需结合时长单位使用
     */
    private Integer duration;

    /**
     * 时长单位 (day, month, year, lifetime)
     * 会员有效期的单位，如天、月、年或终身
     */
    private String durationType;

    /**
     * 方案类型 (free, vip, premium)
     * 会员方案的级别类型
     */
    private String planType;

    /**
     * 是否推荐
     * 标记是否为推荐方案，用于UI展示
     */
    private Boolean isRecommended;

    /**
     * 是否有效
     * 标记会员方案是否可用，false表示已下架
     */
    private Boolean isActive;

    /**
     * 功能列表
     * 该会员方案包含的功能和权益列表
     */
    private List<String> features;
}