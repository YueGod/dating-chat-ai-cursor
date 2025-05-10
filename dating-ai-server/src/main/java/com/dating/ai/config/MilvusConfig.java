package com.dating.ai.config;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.ConnectParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.ai.vectorstore.VectorStore;

/**
 * Milvus向量数据库配置类
 * 配置和初始化Milvus客户端连接和向量存储
 *
 * @author dating-ai
 */
@Configuration
@Slf4j
public class MilvusConfig {

    /**
     * Milvus服务器地址
     */
    @Value("${milvus.host:localhost}")
    private String milvusHost;

    /**
     * Milvus服务端口
     */
    @Value("${milvus.port:19530}")
    private int milvusPort;

    /**
     * 向量维度
     */
    @Value("${milvus.vector.dimension:1536}")
    private int dimension;

    /**
     * 集合名称
     */
    @Value("${milvus.collection:dating_ai_vectors}")
    private String collectionName;

    /**
     * 初始化并配置Milvus客户端
     *
     * @return 配置好的Milvus客户端实例
     */
    @Bean
    public MilvusServiceClient milvusClient() {
        log.info("初始化Milvus客户端连接 - 主机: {}, 端口: {}", milvusHost, milvusPort);

        // 创建连接参数
        ConnectParam connectParam = ConnectParam.newBuilder()
                .withHost(milvusHost)
                .withPort(milvusPort)
                .build();

        // 创建并返回客户端
        return new MilvusServiceClient(connectParam);
    }

    /**
     * 创建Milvus向量存储
     *
     * @param milvusClient   Milvus客户端
     * @param embeddingModel 嵌入模型
     * @return Milvus向量存储实例
     */
    @Bean
    public VectorStore milvusVectorStore(MilvusServiceClient milvusClient, EmbeddingModel embeddingModel) {
        log.info("初始化Milvus向量存储 - 集合: {}, 维度: {}", collectionName, dimension);

        MilvusVectorStore vectorStore = MilvusVectorStore.builder(milvusClient, embeddingModel)
                .collectionName(collectionName)
                .embeddingDimension(dimension)
                .initializeSchema(true)
                .build();
                
        return vectorStore;
    }
}