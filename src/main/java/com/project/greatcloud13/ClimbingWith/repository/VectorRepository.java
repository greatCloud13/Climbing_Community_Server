package com.project.greatcloud13.ClimbingWith.repository;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public interface VectorRepository {

    void save(Embedding embedding, TextSegment textSegment);

    void saveAll(List<Embedding> embeddings, List<TextSegment> textSegment);

    List<TextSegment> search(String query, int maxResults);

}
