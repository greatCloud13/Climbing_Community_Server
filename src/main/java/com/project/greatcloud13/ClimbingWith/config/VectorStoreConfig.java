package com.project.greatcloud13.ClimbingWith.config;

// EC2 배포 환경에 GPU가 없어 임베딩/검색 기능을 임시 비활성화.
// 참고: 여기서 사용하는 Redis(vector 인스턴스)는 Refresh Token 저장용으로도 재사용되므로,
// Redis 컨테이너 자체는 계속 유지해야 함 (비활성화 대상은 이 벡터 스토어 빈뿐).
// 재활성화 시 아래 주석을 해제하면 됨.
/*
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
*/
