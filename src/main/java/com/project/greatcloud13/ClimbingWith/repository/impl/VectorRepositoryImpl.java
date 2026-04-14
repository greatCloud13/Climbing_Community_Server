package com.project.greatcloud13.ClimbingWith.repository.impl;

import com.project.greatcloud13.ClimbingWith.repository.VectorRepository;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingSearchRequest;
import dev.langchain4j.store.embedding.EmbeddingSearchResult;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
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
    public List<String> search(String query, int maxResults) {
        Embedding queryEmbedding = embeddingModel.embed(query).content();

        // 유사도 검색 실행
        EmbeddingSearchRequest request = EmbeddingSearchRequest.builder()
                .queryEmbedding(queryEmbedding)
                .maxResults(maxResults)
                .minScore(0.6) // 아까 테스트한 유사도 기준 적용
                .build();

        EmbeddingSearchResult<TextSegment> result = embeddingStore.search(request);


        return result.matches().stream()
                .map(match -> match.embedded().text())
                .toList();
    }
}
