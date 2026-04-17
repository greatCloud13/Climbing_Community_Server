package com.project.greatcloud13.ClimbingWith.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class VectorStoreConfig {
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(){
        return RedisEmbeddingStore.builder()
                .host("vector")
                .port(6379)
                .dimension(1024) //bge-m3 차원
                .metadataKeys(List.of("postId"))
                .indexName("climbing-knowledge-Index")
                .build();
    }
}
