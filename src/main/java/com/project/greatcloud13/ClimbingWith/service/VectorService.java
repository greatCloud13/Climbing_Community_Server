package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.PostMessage;
import com.project.greatcloud13.ClimbingWith.repository.VectorRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VectorService {

    private final VectorRepository vectorRepository;
    private final EmbeddingModel embeddingModel;

    public void createPostEmbedding(PostMessage message){

//      1. 전처리: HTML 태그 제거 및 공백 정규화
        String preprocessedContent = preprocess(message.getContent());

//      2. 청킹(Chunking): 긴 글을 의미 단위로 분할
//      300자씩 자르고 30자 겹치기
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);
        List<TextSegment> segments = splitter.split(Document.from(preprocessedContent));

//      3. 메타 데이터 추가
//      검색 결과를 통해 원본 게시글 찾아야 하니 ID를 꼬리표로 추가
        segments.forEach(seg -> seg.metadata().put("postId", message.getPostId()));

//      4. 임베딩 생성 및 Redisearch 저장
        List<Embedding> embeddings = embeddingModel.embedAll(segments).content();
        vectorRepository.saveAll(embeddings, segments);

    }

    public List<String> search(String query){
        return vectorRepository.search(query, 3);
    }


    /**
     * 정규식을 이용해 HTML 태그 제거 및 연속된 공백은 하나로 통합
     * @param text 게시글 내용
     * @return 가공된 게시글 문자열
     */
    private String preprocess(String text){
        if(text == null) return "";

        return text.replaceAll("<[^>]*>", " ")
                .replaceAll("\\s+"," ")
                .trim();
    }
}
