package com.dating.ai.service;

import java.util.List;
import java.util.Map;

/**
 * 向量服务接口
 * 定义向量数据库操作方法
 *
 * @author dating-ai
 */
public interface VectorService {

    /**
     * 创建向量集合
     *
     * @param collectionName 集合名称
     * @param distance       距离类型（COSINE, EUCLID, DOT）
     * @return 是否创建成功
     */
    boolean createCollection(String collectionName, String distance);

    /**
     * 删除向量集合
     *
     * @param collectionName 集合名称
     * @return 是否删除成功
     */
    boolean deleteCollection(String collectionName);

    /**
     * 插入向量
     *
     * @param collectionName 集合名称
     * @param vector         向量数据
     * @param metadata       元数据
     * @return 创建的向量ID
     */
    String upsertVector(String collectionName, List<Float> vector, Map<String, Object> metadata);

    /**
     * 插入具有指定ID的向量
     *
     * @param collectionName 集合名称
     * @param id             向量ID
     * @param vector         向量数据
     * @param metadata       元数据
     * @return 是否插入成功
     */
    boolean upsertVector(String collectionName, String id, List<Float> vector, Map<String, Object> metadata);

    /**
     * 搜索相似向量
     *
     * @param collectionName 集合名称
     * @param vector         查询向量
     * @param limit          返回结果限制
     * @param scoreThreshold 相似度阈值
     * @return 搜索结果列表
     */
    List<Map<String, Object>> searchVectors(String collectionName, List<Float> vector, int limit, float scoreThreshold);

    /**
     * 删除向量
     *
     * @param collectionName 集合名称
     * @param id             向量ID
     * @return 是否删除成功
     */
    boolean deleteVector(String collectionName, String id);
}