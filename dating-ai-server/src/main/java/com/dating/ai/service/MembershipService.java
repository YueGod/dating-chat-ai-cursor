package com.dating.ai.service;

import java.util.Map;

/**
 * 会员服务接口
 * 提供会员方案查询、会员管理等功能
 *
 * @author dating-ai
 */
public interface MembershipService {

    /**
     * 获取会员方案列表
     * 根据平台返回对应的会员方案
     *
     * @param platform 平台类型（wechat, android, ios）
     * @return 包含会员方案列表和活动信息的Map
     */
    Map<String, Object> getMembershipPlans(String platform);

    /**
     * 取消会员自动续费
     * 关闭当前用户的会员自动续费功能
     *
     * @param cancelReason 取消原因，用于统计和分析
     * @return 包含操作结果的Map
     */
    Map<String, Object> cancelRenewal(String cancelReason);

    /**
     * 更新用户会员状态
     * 在订单支付成功后调用，更新用户会员状态
     *
     * @param orderId 支付成功的订单ID
     * @return 包含更新结果的Map
     */
    Map<String, Object> updateMembershipStatus(String orderId);

    /**
     * 检查会员权限
     * 验证用户是否有权限访问VIP功能
     *
     * @param styleId 可选的风格ID，用于检查特定风格的访问权限
     * @return 如果用户有权限访问则返回true，否则返回false
     */
    boolean checkVipAccess(String styleId);
}