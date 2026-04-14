package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.hibernate.boot.model.naming.IllegalIdentifierException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final GymRepository gymRepository;
    private final UserRepository userRepository;
    private final RabbitTemplate rabbitTemplate;

    /**
     * 신규 게시글을 작성, 임베딩
     * [Business Rule]
     * 1. 요청한 사용자가 존재하고 해당 지점의 관리권한이 있어야합니다.
     *
     * @param userId 사용자 ID
     * @param requestDTO 게시글 정보
     * @return 생성된 게시글 DTO
     */
    @Transactional
    public PostResponseDTO createPost(Long userId, PostCreateDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if(!user.isManager()){
            throw new IllegalArgumentException("잘못된 접근 입니다.");
        }

        Post post = Post.builder()
                .user(user)
                .gym(user.getGym())
                .user(user)
                .title(requestDTO.getTitle())
                .postType(requestDTO.getPostType())
                .content(requestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);

//      RabbitMQ 메시지 전달
        PostMessage message = new PostMessage(post.getId(), post.getContent());
        rabbitTemplate.convertAndSend(
                "post.embedding.exchange",
                "post.embedding.key",
                message
        );

        return PostResponseDTO.from(post);
    }

    @Transactional
    public PostResponseDTO updatePost(Long userId, Long postId, PostUpdateDTO request){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("존재하지 않는 사용자 입니다."));

        if(!user.isManager()){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new EntityNotFoundException("게시글을 찾을 수 없습니다."));


        post.validateWriter(userId);
        post.updatePost(request, LocalDateTime.now());

        return PostResponseDTO.from(post);
    }

    @Transactional(readOnly = true)
    public PostResponseDTO getPostById(Long postId){

        Post post = postRepository.findById(postId)
                .orElseThrow(()->new EntityNotFoundException("게시글을 찾을 수 없습니다."));

        return PostResponseDTO.from(post);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryDTO> getAllByGym(Long gymId, Pageable pageable){

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(()->new EntityNotFoundException("암장을 찾을 수 없습니다."));

        Page<Post> postPage = postRepository.findAllByGym(gym, pageable);

        return postPage.map(PostSummaryDTO :: from);
    }

    @Transactional(readOnly = true)
    public Page<PostSummaryDTO> getAllByGymWithPostType(Long gymId, PostType postType, Pageable pageable){
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(()->new EntityNotFoundException("암장을 찾을 수 없습니다."));

        Page<Post> postPage = postRepository.findAllByGymAndPostType(gym, postType, pageable);

        return postPage.map(PostSummaryDTO::from);
    }

}
