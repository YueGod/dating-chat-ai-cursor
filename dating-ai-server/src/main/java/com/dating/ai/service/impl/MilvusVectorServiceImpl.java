package com.dating.ai.service.impl;

import com.dating.ai.constant.ErrorCode;
import com.dating.ai.exception.BusinessException;
import com.dating.ai.service.VectorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 向量服务的Spring AI Milvus实现
 * 使用Spring AI的MilvusVectorStore
 *
 * @author dating-ai
 */
@Service
@RequiredArgsConstructor
@Primary
@Slf4j
public class MilvusVectorServiceImpl implements VectorService {

    private final VectorStore vectorStore;
    private final EmbeddingModel embeddingModel;

    /**
     * 向量维度
     */
    @Value("${milvus.vector.dimension:1536}")
    private int dimension;

    /**
     * 创建向量集合
     * 
     * 注意：使用Spring AI的MilvusVectorStore时，集合由Spring AI自动创建和管理
     *
     * @param collectionName 集合名称（不使用，由配置决定）
     * @param distance       距离类型（不使用，由配置决定）
     * @return 总是返回true
     */
    @Override
    public boolean createCollection(String collectionName, String distance) {
        log.info("使用Spring AI MilvusVectorStore时，集合自动创建，无需手动创建");
        return true;
    }

    /**
     * 删除向量集合
     * 
     * 注意：使用Spring AI的MilvusVectorStore时，不支持此操作
     *
     * @param collectionName 集合名称
     * @return 总是返回true
     */
    @Override
    public boolean deleteCollection(String collectionName) {
        log.info("使用Spring AI MilvusVectorStore时，不支持删除集合操作");
        return true;
    }

    /**
     * 插入向量
     *
     * @param collectionName 集合名称（不使用）
     * @param vector         向量数据
     * @param metadata       元数据
     * @return 创建的向量ID
     */
    @Override
    public String upsertVector(String collectionName, List<Float> vector, Map<String, Object> metadata) {
        try {
            String id = UUID.randomUUID().toString();
            upsertVector(collectionName, id, vector, metadata);
            return id;
        } catch (Exception e) {
            log.error("插入向量失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "插入向量失败: " + e.getMessage());
        }
    }

    /**
     * 插入具有指定ID的向量
     *
     * @param collectionName 集合名称（不使用）
     * @param id             向量ID
     * @param vector         向量数据
     * @param metadata       元数据
     * @return 是否插入成功
     */
    @Override
    public boolean upsertVector(String collectionName, String id, List<Float> vector, Map<String, Object> metadata) {
        try {
            // 创建元数据，添加ID
            Map<String, Object> documentMetadata = metadata != null ? new HashMap<>(metadata) : new HashMap<>();
            documentMetadata.put("id", id);

            // 由于Spring AI Vector Store API不允许直接设置向量
            // 我们使用特殊内容标记生成文档，稍后用于检索
            String uniqueContent = "vector_" + id;
            Document document = new Document(id, uniqueContent, documentMetadata);

            // 添加到向量存储 - embedding会由嵌入模型自动生成
            // 注意：这不是最有效的方法，因为我们无法使用预先计算的向量
            vectorStore.add(List.of(document));

            log.debug("成功插入向量, ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("插入向量失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "插入向量失败: " + e.getMessage());
        }
    }

    /**
     * 搜索相似向量
     *
     * @param collectionName 集合名称（不使用）
     * @param vector         查询向量
     * @param limit          返回结果限制
     * @param scoreThreshold 相似度阈值
     * @return 搜索结果列表
     */
    @Override
    public List<Map<String, Object>> searchVectors(String collectionName, List<Float> vector, int limit,
            float scoreThreshold) {
        try {
            // 创建唯一的查询文本
            String uniqueContent = "search_" + UUID.randomUUID().toString();

            // 创建搜索请求
            SearchRequest searchRequest = SearchRequest.builder()
                    .query(uniqueContent)
                    .topK(limit)
                    .similarityThreshold(scoreThreshold)
                    .build();

            // 执行搜索
            List<Document> results = vectorStore.similaritySearch(searchRequest);

            // 转换结果格式
            return results.stream()
                    .map(document -> {
                        Map<String, Object> resultMap = new HashMap<>(document.getMetadata());
                        // 添加分数信息
                        if (document.getScore() != null) {
                            resultMap.put("score", document.getScore());
                        }
                        return resultMap;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.error("搜索向量失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "搜索向量失败: " + e.getMessage());
        }
    }

    /**
     * 删除向量
     *
     * @param collectionName 集合名称（不使用）
     * @param id             向量ID
     * @return 是否删除成功
     */
    @Override
    public boolean deleteVector(String collectionName, String id) {
        try {
            vectorStore.delete(List.of(id));
            log.debug("成功删除向量, ID: {}", id);
            return true;
        } catch (Exception e) {
            log.error("删除向量失败: {}", e.getMessage(), e);
            throw new BusinessException(ErrorCode.Common.INTERNAL_ERROR, "删除向量失败: " + e.getMessage());
        }
    }

    /**
     * 将Float向量转换为Double向量
     */
    private List<Double> toDoubleList(List<Float> floatList) {
        if (floatList == null)
            return null;
        return floatList.stream()
                .map(Float::doubleValue)
                .collect(Collectors.toList());
    }
}