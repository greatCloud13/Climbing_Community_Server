package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.dto.PostSummaryDTO;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;

import java.util.List;

public interface VectorRepository {

    void save(Embedding embedding, TextSegment textSegment);

    void saveAll(List<Embedding> embeddings, List<TextSegment> textSegment);

    List<TextSegment> search(String query, int maxResults);

    List<TextSegment> searchByGym(String query, Long gymId, int maxResults);

    List<TextSegment> searchByPostTypeWithGym(String query, Long gymId, PostType postType, int maxResults);

    List<TextSegment> searchByPostType(String query, PostType postType, int maxResults);
}
