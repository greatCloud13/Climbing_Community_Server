package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.PostMessage;
import com.project.greatcloud13.ClimbingWith.dto.PostSummaryDTO;
import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.post.TooManyRequestsException;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
import com.project.greatcloud13.ClimbingWith.repository.VectorRepository;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
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
    private final GymRepository gymRepository;

    public void createPostEmbedding(PostMessage message){

//      1. 전처리: HTML 태그 제거 및 공백 정규화
        String preprocessedContent = preprocess(message.getContent());

//      2. 청킹(Chunking): 긴 글을 의미 단위로 분할
//      300자씩 자르고 30자 겹치기
        DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);
        List<TextSegment> segments = splitter.split(Document.from(preprocessedContent));

//      3. "[제목] 내용 "
//      형태로 세그먼트별로 타이틀 주입
        String title = message.getPostTitle();
        List<TextSegment> injectedSegments = segments.stream()
                .map(seg -> {
                    String injectedText = String.format("[%s]\n%s", title, seg.text());
//                  메타데이터 주입
                    return TextSegment.from(injectedText, seg.metadata());
                })
                .toList();

//      4. 게시글 ID 메타 데이터 추가
//      검색 결과를 통해 원본 게시글 찾아야 하니 ID를 꼬리표로 추가
        injectedSegments.forEach(seg -> seg.metadata().put("postId", message.getPostId()));

//      4. 메타 데이터 게시글 타입 추가
        injectedSegments.forEach(seg -> seg.metadata().put("postType", message.getPostType().name()));

//      5. 메타데이터 암장 이름 추가
        injectedSegments.forEach(seg -> seg.metadata().put("gymName", message.getGymName()));

//      6. 임베딩 생성 및 Redisearch 저장
        List<Embedding> embeddings = embeddingModel.embedAll(injectedSegments).content();
        vectorRepository.saveAll(embeddings, segments);

    }

    /**
     * 게시글을 검색합니다
     *
     * @param query 검색어
     * @param count 검색 갯수
     * @return 검색결과
     * @throws TooManyRequestsException 검색갯수가 10개 이상일 경우
     */
    public List<PostSummaryDTO> postSearch(String query, int count){

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
     * 요청한 암장의 게시글에서 검색을 시도합니다.
     *
     * @param query 검색어
     * @param count 검색 갯수
     * @param gymId 암장 ID
     * @return 검색결과
     * @throws TooManyRequestsException 검색갯수가 10개 이상일 경우
     * @throws GymNotFoundException 요청한 암장이 존재하지 않는 경우
     */
    public List<PostSummaryDTO> postSearchWithGym(String query, int count, Long gymId){

//      1. 자원을 고려하여 요청 갯수는 10개 이하로만 요청 가능하도록 구현
        if(count>10){
            throw new TooManyRequestsException();
        }

        if(!gymRepository.existsById(gymId)){
            throw new GymNotFoundException();
        }

//      2. 검색 수행 및 검색된 PostId 추출
        List<TextSegment> relevantSegments = vectorRepository.searchByGym(query, gymId, count);
        List<Long> postIdList = relevantSegments.stream().map(
                seg -> seg.metadata().getLong("postId"))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Post> postList = postRepository.findAllById(postIdList);

        return postList.stream().map(PostSummaryDTO :: from).toList();
    }


    /**
     * 전체 암장의 게시글중 요청한 PostType과 일치하는 게시글내에서 검색
     *
     * @param query 검색어
     * @param postType 게시글 타입
     * @param count 검색 갯수
     * @return 검색결과
     * @throws TooManyRequestsException 검색갯수가 10개 이상일 경우
     * @throws GymNotFoundException 요청한 암장이 존재하지 않는 경우
     */
    public List<PostSummaryDTO> postSearchByPostType(String query, PostType postType, int count){
//      1. 자원을 고려하여 요청 갯수는 10개 이하로만 요청 가능하도록 구현
        if(count>10){
            throw new TooManyRequestsException();
        }

//      2. 검색 수행 및 검색된 PostId 추출
        List<TextSegment> relevantSegments = vectorRepository.searchByPostType(query, postType, count);
        List<Long> postIdList = relevantSegments.stream().map(
                        seg -> seg.metadata().getLong("postId"))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Post> postList = postRepository.findAllById(postIdList);

        return postList.stream().map(PostSummaryDTO :: from).toList();
    }

    /**
     * 요청한 암장 ID와 일치하는 게시글 중 요청한 PostType과 일치하는 게시글내에서 검색
     *
     * @param query 검색어
     * @param gymId 암장 ID
     * @param postType 게시글 타입
     * @param count 검색 갯수
     * @return 검색결과
     * @throws TooManyRequestsException 검색갯수가 10개 이상일 경우
     * @throws GymNotFoundException 요청한 암장이 존재하지 않는 경우
     */
    public List<PostSummaryDTO> postSearchByPostTypeWithGym(String query, Long gymId, PostType postType, int count){
//      1. 자원을 고려하여 요청 갯수는 10개 이하로만 요청 가능하도록 구현
        if(count>10){
            throw new TooManyRequestsException();
        }

        if(!gymRepository.existsById(gymId)){
            throw new GymNotFoundException();
        }

//      2. 검색 수행 및 검색된 PostId 추출
        List<TextSegment> relevantSegments = vectorRepository.searchByPostTypeWithGym(query, gymId, postType, count);
        List<Long> postIdList = relevantSegments.stream().map(
                        seg -> seg.metadata().getLong("postId"))
                .filter(Objects::nonNull)
                .distinct()
                .toList();

        List<Post> postList = postRepository.findAllById(postIdList);

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
