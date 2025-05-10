package com.dating.ai.dao;

import com.dating.ai.domain.MembershipPlan;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 会员方案仓库接口
 * 提供会员方案相关的数据访问功能
 *
 * @author dating-ai
 */
@Repository
public interface MembershipPlanRepository extends MongoRepository<MembershipPlan, String> {

    /**
     * 根据方案ID查找会员方案
     * 通过唯一的方案ID精确查找对应的会员方案
     *
     * @param planId 方案ID
     * @return 包含会员方案的Optional对象，若不存在则为空
     */
    Optional<MembershipPlan> findByPlanId(String planId);

    /**
     * 查找所有有效的会员方案
     * 查询所有isActive为true的会员方案，并按指定规则排序
     *
     * @param sort 排序规则，如按价格、推荐度等排序
     * @return 有效的会员方案列表
     */
    List<MembershipPlan> findByIsActiveTrue(Sort sort);

    /**
     * 根据方案类型查找会员方案
     * 查询指定类型且有效的会员方案，如VIP、高级会员等
     *
     * @param planType 方案类型，如"free"、"vip"、"premium"等
     * @param sort     排序规则，如按价格、持续时间等排序
     * @return 指定类型的有效会员方案列表
     */
    List<MembershipPlan> findByPlanTypeAndIsActiveTrue(String planType, Sort sort);

    /**
     * 查找推荐的会员方案
     * 查询所有被标记为推荐且有效的会员方案
     *
     * @param sort 排序规则，如按价格、推荐度等排序
     * @return 推荐的有效会员方案列表
     */
    List<MembershipPlan> findByIsRecommendedTrueAndIsActiveTrue(Sort sort);
}