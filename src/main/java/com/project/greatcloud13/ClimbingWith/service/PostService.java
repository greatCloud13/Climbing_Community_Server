package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.PostCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostResponseDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDTO createPost(Long userId, PostCreateDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

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

        return PostResponseDTO.from(post);
    }

    @Transactional
    public PostResponseDTO updatePost(Long userId, Long postId, PostUpdateDTO request){
        return null;
    }
}
