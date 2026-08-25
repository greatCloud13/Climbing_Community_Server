package com.project.greatcloud13.ClimbingWith.config;

// EC2 배포 환경에 GPU가 없어 임베딩 모델(Ollama) 및 관련 검색 기능을 임시 비활성화.
// 재활성화 시 아래 주석을 해제하면 됨.
/*
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.ollama.OllamaEmbeddingModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingModelConfig {

    @Bean
    public EmbeddingModel embeddingModel(){
        return OllamaEmbeddingModel.builder()
                .baseUrl("http://host.docker.internal:11434")
                .modelName("bge-m3")
                .build();
    }

}
*/
