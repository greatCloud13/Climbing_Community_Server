package com.project.greatcloud13.ClimbingWith.rabbitmq;

import com.project.greatcloud13.ClimbingWith.config.RabbitMQConfig;
import com.project.greatcloud13.ClimbingWith.dto.PostMessage;
import com.project.greatcloud13.ClimbingWith.service.VectorService;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PostEmbeddingConsumer {

    private final EmbeddingStore<TextSegment> embeddingStore;
    private final EmbeddingModel embeddingModel;
    private final VectorService vectorService;

    @RabbitListener(queues = RabbitMQConfig.QUEUE_NAME)
    public void handlePostEmbedding(PostMessage message){
        long start = System.currentTimeMillis();
        log.info("전처리 가공 시작 (게시글 ID: {})", message.getPostId());

        try{
            vectorService.createPostEmbedding(message);
            long elapsed = System.currentTimeMillis() - start;
            log.info("게시글(ID: {}) 임베딩 완료 - 소요시간: {}ms", message.getPostId(), elapsed);
        }catch (Exception e){
            log.error("인덱싱 중 에러 발생");
        }
    }

}
