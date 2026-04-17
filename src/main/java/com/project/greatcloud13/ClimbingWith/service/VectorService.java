package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.PostMessage;
import com.project.greatcloud13.ClimbingWith.dto.PostSearchResultSummaryDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostSummaryDTO;
import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.exception.post.TooManyRequestsException;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@Slf4j
@RequiredArgsConstructor
public class VectorService {

    private final VectorRepository vectorRepository;
    private final EmbeddingModel embeddingModel;
    private final PostRepository postRepository;

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


    public List<PostSummaryDTO> postSearchAndGetSummary(String query, int count){

//      1. 자원을 고려하여 요청 갯수가 10개 이하로만 요청 가능하도록 구현
        if(count>10){
            throw new TooManyRequestsException();
        }

//      2. 검색 수행 및 검색된 PostId 추출
        List<TextSegment> relevantSegments = vectorRepository.search(query, count);
        List<Long> postIdList = relevantSegments.stream().map(
                segment -> segment.metadata().getLong("postId"))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        log.info("조회시도 id list: {}", postIdList);
        log.info("검색결과: {}", postIdList.size());

//      3. 검색된 PostId를 통해 게시글 검색
        List<Post> postList = postRepository.findAllById(postIdList);

        log.info("게시물 조회: {}", postList);

        return postList.stream().map(PostSummaryDTO :: from).toList();
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
