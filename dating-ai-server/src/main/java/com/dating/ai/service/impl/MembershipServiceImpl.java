package com.dating.ai.service.impl;

import com.dating.ai.constant.ErrorCode;
import com.dating.ai.dao.MembershipPlanRepository;
import com.dating.ai.dao.OrderRepository;
import com.dating.ai.dao.UserMembershipRepository;
import com.dating.ai.domain.MembershipPlan;
import com.dating.ai.domain.Order;
import com.dating.ai.domain.UserMembership;
import com.dating.ai.exception.BusinessException;
import com.dating.ai.service.MembershipService;
import com.dating.ai.utils.UserContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * 会员服务实现类
 * 提供会员方案查询、会员管理等功能
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MembershipServiceImpl implements MembershipService {

    private final MembershipPlanRepository membershipPlanRepository;
    private final UserMembershipRepository userMembershipRepository;
    private final OrderRepository orderRepository;

    /**
     * 获取会员方案列表
     * 根据平台返回对应的会员方案
     *
     * @param platform 平台类型（wechat, android, ios）
     * @return 包含会员方案列表和活动信息的Map
     */
    @Override
    public Map<String, Object> getMembershipPlans(String platform) {
        // 根据平台类型获取对应的会员方案
        // 所有平台都可以使用的方案
        List<MembershipPlan> plans = membershipPlanRepository.findByIsActiveTrue(
                Sort.by(Sort.Direction.ASC, "price"));

        // 获取当前用户的会员状态
        String userId = UserContext.getUserId();
        Optional<UserMembership> currentMembership = userMembershipRepository.findByUserIdAndIsActiveTrue(userId);

        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("plans", plans);

        // 添加活动信息（如有）
        Map<String, Object> promotionInfo = getPromotionInfo(platform);
        if (!promotionInfo.isEmpty()) {
            result.put("promotions", promotionInfo);
        }

        // 添加用户会员状态
        Map<String, Object> membershipStatus = new HashMap<>();
        currentMembership.ifPresent(membership -> {
            membershipStatus.put("isVip", true);
            membershipStatus.put("planId", membership.getPlanId());
            membershipStatus.put("planName", membership.getPlanName());
            membershipStatus.put("expireTime", membership.getExpireTime());
            membershipStatus.put("autoRenew", membership.getAutoRenew());
            if (membership.getAutoRenew()) {
                membershipStatus.put("nextBillingDate", membership.getNextBillingDate());
                membershipStatus.put("nextBillingAmount", membership.getNextBillingAmount());
            }
        });

        if (membershipStatus.isEmpty()) {
            membershipStatus.put("isVip", false);
        }

        result.put("userMembership", membershipStatus);

        return result;
    }

    /**
     * 取消会员自动续费
     * 关闭当前用户的会员自动续费功能
     *
     * @param cancelReason 取消原因，用于统计和分析
     * @return 包含操作结果的Map
     */
    @Override
    @Transactional
    public Map<String, Object> cancelRenewal(String cancelReason) {
        String userId = UserContext.getUserId();

        // 查找用户当前有效会员
        UserMembership membership = userMembershipRepository.findByUserIdAndIsActiveTrue(userId)
                .orElseThrow(() -> new BusinessException(ErrorCode.Membership.MEMBERSHIP_NOT_FOUND, "未找到有效的会员"));

        // 检查是否已经设置为不自动续费
        if (!membership.getAutoRenew()) {
            throw new BusinessException(ErrorCode.Membership.ALREADY_CANCELED_RENEWAL, "会员已经设置为不自动续费");
        }

        // 更新会员信息
        membership.setAutoRenew(false);
        membership.setCancelReason(cancelReason);
        userMembershipRepository.save(membership);

        // 记录日志
        log.info("用户 {} 取消了会员自动续费，原因: {}", userId, cancelReason);

        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "已成功取消自动续费");
        result.put("cancelTime", new Date());
        result.put("expireTime", membership.getExpireTime());

        return result;
    }

    /**
     * 更新用户会员状态
     * 在订单支付成功后调用，更新用户会员状态
     *
     * @param orderId 支付成功的订单ID
     * @return 包含更新结果的Map
     */
    @Override
    @Transactional
    public Map<String, Object> updateMembershipStatus(String orderId) {
        // 查找订单
        Order order = orderRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.Order.ORDER_NOT_FOUND, "订单不存在"));

        // 验证订单状态
        if (!"paid".equals(order.getOrderStatus())) {
            throw new BusinessException(ErrorCode.Order.ORDER_NOT_PAID, "订单尚未支付成功");
        }

        // 查找会员方案
        MembershipPlan plan = membershipPlanRepository.findByPlanId(order.getPlanId())
                .orElseThrow(() -> new BusinessException(ErrorCode.Membership.PLAN_NOT_FOUND, "会员方案不存在"));

        // 获取当前用户的会员信息
        String userId = order.getUserId();
        Optional<UserMembership> existingMembership = userMembershipRepository.findByUserIdAndIsActiveTrue(userId);

        UserMembership membership;
        Date startTime = new Date();
        Date expireTime;

        if (existingMembership.isPresent()) {
            // 如果用户已有会员，则延长会员期限
            membership = existingMembership.get();

            // 如果当前会员尚未到期，则从到期时间开始计算新的到期时间
            if (membership.getExpireTime().after(startTime)) {
                startTime = membership.getExpireTime();
            }
        } else {
            // 创建新的会员记录
            membership = new UserMembership();
            membership.setUserId(userId);
            membership.setIsActive(true);
        }

        // 计算到期时间
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(startTime);

        switch (plan.getDurationType()) {
            case "day":
                calendar.add(Calendar.DAY_OF_MONTH, plan.getDuration());
                break;
            case "month":
                calendar.add(Calendar.MONTH, plan.getDuration());
                break;
            case "year":
                calendar.add(Calendar.YEAR, plan.getDuration());
                break;
            case "lifetime":
                // 设置一个很远的将来日期，例如100年后
                calendar.add(Calendar.YEAR, 100);
                break;
            default:
                throw new BusinessException(ErrorCode.Membership.UNSUPPORTED_DURATION_TYPE,
                        "不支持的时长单位: " + plan.getDurationType());
        }

        expireTime = calendar.getTime();

        // 更新会员信息
        membership.setPlanId(plan.getPlanId());
        membership.setPlanName(plan.getPlanName());
        membership.setStartTime(startTime);
        membership.setExpireTime(expireTime);
        membership.setOriginalOrderId(orderId);
        membership.setAutoRenew(order.getAutoRenew());

        // 如果设置了自动续费，计算下次扣费日期和金额
        if (Boolean.TRUE.equals(order.getAutoRenew())) {
            membership.setNextBillingDate(expireTime);
            membership.setNextBillingAmount(order.getFinalAmount());
        } else {
            membership.setNextBillingDate(null);
            membership.setNextBillingAmount(null);
        }

        // 保存会员信息
        userMembershipRepository.save(membership);

        // 记录日志
        log.info("用户 {} 成功开通/续费会员, 方案: {}, 到期时间: {}", userId, plan.getPlanName(), expireTime);

        // 构建响应
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "会员已成功开通/续费");
        result.put("userId", userId);
        result.put("planId", plan.getPlanId());
        result.put("planName", plan.getPlanName());
        result.put("startTime", startTime);
        result.put("expireTime", expireTime);
        result.put("autoRenew", membership.getAutoRenew());

        return result;
    }

    /**
     * 检查会员权限
     * 验证用户是否有权限访问VIP功能
     *
     * @param styleId 可选的风格ID，用于检查特定风格的访问权限
     * @return 如果用户有权限访问则返回true，否则返回false
     */
    @Override
    public boolean checkVipAccess(String styleId) {
        String userId = UserContext.getUserId();

        // 查找用户当前有效会员
        Optional<UserMembership> membership = userMembershipRepository.findByUserIdAndIsActiveTrue(userId);

        // 如果没有会员或会员已过期，返回false
        if (membership.isEmpty() || membership.get().getExpireTime().before(new Date())) {
            return false;
        }

        // 如果有指定styleId，需要检查该风格是否需要高级会员权限
        if (styleId != null && !styleId.isEmpty()) {
            // 实现风格权限检查逻辑
            // 这里可以查询风格表，检查该风格是否需要特定会员等级
            // 简化处理，假设所有风格都可访问
            return true;
        }

        // 用户有有效会员，允许访问
        return true;
    }

    /**
     * 获取活动信息
     * 根据平台获取当前有效的促销活动信息
     *
     * @param platform 平台类型
     * @return 活动信息Map
     */
    private Map<String, Object> getPromotionInfo(String platform) {
        // 这里可以实现获取特定平台的促销活动逻辑
        // 简化处理，返回一个示例促销活动
        Map<String, Object> promotionInfo = new HashMap<>();

        // 生成随机的促销活动（示例）
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7); // 7天后结束

        promotionInfo.put("promotionId", "spring_festival_2024");
        promotionInfo.put("promotionName", "春节特惠");
        promotionInfo.put("description", "春节期间会员特惠，全场8折");
        promotionInfo.put("discountRate", 0.8);
        promotionInfo.put("startTime", new Date());
        promotionInfo.put("endTime", calendar.getTime());

        return promotionInfo;
    }
}