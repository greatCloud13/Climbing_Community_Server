package com.project.greatcloud13.ClimbingWith.config;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class LangChainConfig {

    @Bean
    public ChatLanguageModel chatLanguageModel(){
        return OllamaChatModel.builder()
                .baseUrl("http://host.docker.internal:11434")
                .modelName("gemma2")
                .timeout(Duration.ofSeconds(60)) // 모델 응답 대기 시간 설정
                .temperature(0.7)
                .build();
    }

}
