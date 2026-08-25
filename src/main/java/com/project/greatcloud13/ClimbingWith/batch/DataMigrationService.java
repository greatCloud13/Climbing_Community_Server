package com.project.greatcloud13.ClimbingWith.batch;

// EC2 배포 환경에 GPU가 없어 임베딩 기능을 임시 비활성화함에 따라,
// 임베딩 생성에 의존하는 기존 게시글 마이그레이션 기능도 함께 비활성화.
// 재활성화 시 아래 주석을 해제하면 됨.
/*
import com.project.greatcloud13.ClimbingWith.dto.PostMessage;
import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.common.AccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.service.VectorService;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Slf4j
public class DataMigrationService {

    private final PostRepository postRepository;
    private final VectorService vectorService;
    private final UserRepository userRepository;
    private final DocumentSplitter splitter = DocumentSplitters.recursive(300, 30);

    @Transactional(readOnly = true)
    public void migrationExistingPosts(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if(!user.isAdmin()){
            throw new AccessDeniedException();
        }

        try(Stream<Post> postStream =postRepository.streamAllBy()){
            postStream.forEach(post -> {
                PostMessage message = PostMessage.from(post);
                vectorService.createPostEmbedding(message);
            });
        } catch (Exception e) {
            log.error("마이그레이션 실패: {}", e.getMessage());
        }
    }
}
*/
