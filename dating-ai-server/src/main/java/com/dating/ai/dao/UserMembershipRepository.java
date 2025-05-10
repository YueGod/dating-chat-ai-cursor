package com.dating.ai.dao;

import com.dating.ai.domain.UserMembership;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户会员信息仓库接口
 * 提供用户会员相关的数据访问功能
 *
 * @author dating-ai
 */
@Repository
public interface UserMembershipRepository extends MongoRepository<UserMembership, String> {

    /**
     * 根据用户ID查找有效的会员信息
     * 查询指定用户当前有效的会员记录
     *
     * @param userId 用户ID
     * @return 包含用户会员信息的Optional对象，若不存在则为空
     */
    Optional<UserMembership> findByUserIdAndIsActiveTrue(String userId);

    /**
     * 根据用户ID查找所有会员记录
     * 查询指定用户的所有会员记录，包括历史记录
     *
     * @param userId 用户ID
     * @return 用户的所有会员记录列表
     */
    java.util.List<UserMembership> findByUserId(String userId);

    /**
     * 根据订单ID查找会员记录
     * 查询通过特定订单创建的会员记录
     *
     * @param originalOrderId 原始订单ID
     * @return 包含会员信息的Optional对象，若不存在则为空
     */
    Optional<UserMembership> findByOriginalOrderId(String originalOrderId);
}