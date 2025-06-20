package com.ms.spring_ai_rag_wallet_assisstant;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import co.elastic.clients.elasticsearch.ElasticsearchClient;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreConfig;

@Configuration
public class ElasticsearchVectorStoreConfig {

    @Bean
    public ElasticsearchVectorStore elasticsearchVectorStore(ElasticsearchClient elasticsearchClient, @Value("${spring.ai.vectorstore.elasticsearch.index-name:vector_index}") String indexName) {
        ElasticsearchVectorStoreConfig config = new ElasticsearchVectorStoreConfig(indexName);
        return new ElasticsearchVectorStore(elasticsearchClient, config);
    }
}
