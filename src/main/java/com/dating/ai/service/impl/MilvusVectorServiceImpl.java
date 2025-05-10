// ... existing code ...
            // 将向量数据转换为Document
            // 注意：Spring AI的Document需要内容，但我们只关心向量，所以使用ID作为内容
            Document document = new Document(id, documentMetadata);

            // 设置自定义向量（跳过嵌入模型）
            List<Double> doubleVector = vector.stream()
                    .map(Float::doubleValue)
                    .collect(Collectors.toList());
            // M8版本不再使用setEmbedding方法，直接在构造函数中设置embedding
            Document documentWithEmbedding = new Document(id, id, doubleVector, documentMetadata);

            // 添加到向量存储
            vectorStore.add(List.of(documentWithEmbedding));
// ... existing code ...

            // 创建搜索请求
            SearchRequest searchRequest = SearchRequest.builder()
                    .withVector(doubleVector)
                    .withTopK(limit)
                    .withSimilarityThreshold(scoreThreshold)
                    .build();
// ... existing code ...