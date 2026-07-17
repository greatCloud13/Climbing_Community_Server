package com.project.greatcloud13.ClimbingWith.config;

import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.redis.RedisEmbeddingStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class VectorStoreConfig {
    @Bean
    public EmbeddingStore<TextSegment> embeddingStore(
            @Value("${vector.redis.host}") String host,
            @Value("${vector.redis.port}") int port) {
        return RedisEmbeddingStore.builder()
                .host(host)
                .port(port)
                .dimension(1024) //bge-m3 차원
                .metadataKeys(List.of("postId"))
                .indexName("climbing-knowledge-Index")
                .build();
    }
}
