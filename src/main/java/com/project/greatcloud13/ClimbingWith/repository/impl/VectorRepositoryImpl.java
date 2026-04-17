package com.project.greatcloud13.ClimbingWith.repository.impl;

import com.project.greatcloud13.ClimbingWith.repository.VectorRepository;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
@Slf4j
public class VectorRepositoryImpl implements VectorRepository {

    private final EmbeddingModel embeddingModel;
    private final EmbeddingStore<TextSegment> embeddingStore;

    @Override
    public void save(Embedding embedding, TextSegment textSegment) {

        embeddingStore.add(embedding, textSegment);
    }

    public void saveAll(List<Embedding> embeddings, List<TextSegment> textSegments){

        embeddingStore.addAll(embeddings, textSegments);
    }

    @Override
    public List<TextSegment> search(String query, int maxResults) {

//      1. 검색 질문 벡터로 변환 (임베딩 모델과 같은 모델을 사용해야한다.)
        Embedding queryEmbedding = embeddingModel.embed(query).content();
        log.info("검색어: {}, 검색 갯수: {}", query, maxResults);

//      2. 유사도 검색 실행
//      minScore를 통해 너무 관련 없는 결과는 제거하도록 함
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .minScore(0.6) // 유사도 최소 0.6
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);

        log.info("검색결과 여부: {}", result.matches().stream().findFirst().isPresent());
        log.info("검색된 메타데이터: {}", result.matches().stream()
                .map(match -> match.embedded().metadata().toMap()) // 맵으로 변환해서 출력
                .toList());
        log.info("검색 결과: {}", result.matches().stream()
                .map(EmbeddingMatch::embedded)
                .toList());

//      3. 검색된 텍스트 조각들을 리스트로 변환
        return result.matches().stream()
                .map(EmbeddingMatch::embedded)
                .toList();
    }
}
