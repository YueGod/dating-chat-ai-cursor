package com.dating.ai.dao;

import com.dating.ai.domain.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * 订单仓库接口
 * 提供订单相关的数据访问功能
 *
 * @author dating-ai
 */
@Repository
public interface OrderRepository extends MongoRepository<Order, String> {

    /**
     * 根据订单ID查找订单
     * 通过唯一的订单ID精确查找对应的订单记录
     *
     * @param orderId 订单ID
     * @return 包含订单的Optional对象，若不存在则为空
     */
    Optional<Order> findByOrderId(String orderId);

    /**
     * 根据用户ID查找订单列表，按创建时间降序
     * 分页查询指定用户的所有订单，按订单创建时间从新到旧排序
     *
     * @param userId   用户ID
     * @param pageable 分页信息，包括页码、每页大小等
     * @return 分页的订单列表结果
     */
    Page<Order> findByUserIdOrderByCreateTimeDesc(String userId, Pageable pageable);

    /**
     * 根据支付交易ID查找订单
     * 通过支付平台返回的交易ID查找对应的订单记录
     *
     * @param paymentTransactionId 支付交易ID，支付平台返回的唯一标识
     * @return 包含订单的Optional对象，若不存在则为空
     */
    Optional<Order> findByPaymentTransactionId(String paymentTransactionId);

    /**
     * 查找过期未支付的订单
     * 查询指定状态且已过期的订单列表，用于定时清理或关闭过期订单
     *
     * @param status     订单状态，通常为"created"(已创建)状态
     * @param expireTime 过期时间，查询早于此时间的订单
     * @return 符合条件的过期订单列表
     */
    List<Order> findByOrderStatusAndExpireTimeBefore(String status, Date expireTime);

    /**
     * 查找用户最近一笔支付成功的订单
     * 分页查询指定用户特定状态的订单，按支付时间从新到旧排序
     * 通常用于获取用户最近的有效订单信息
     *
     * @param userId   用户ID
     * @param status   订单状态，通常为"paid"(已支付)状态
     * @param pageable 分页信息，通常设置为一页一条记录以获取最新订单
     * @return 分页的订单列表结果
     */
    Page<Order> findByUserIdAndOrderStatusOrderByPaymentTimeDesc(String userId, String status, Pageable pageable);
}