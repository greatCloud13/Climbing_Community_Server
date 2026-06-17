package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.PostCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostResponseDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostSummaryDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.post.PostAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.post.PostNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final GymRepository gymRepository;
    private final UserRepository userRepository;

    @Transactional
    public PostResponseDTO createPost(Long userId, PostCreateDTO requestDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isManager()) {
            throw new PostAccessDeniedException();
        }

        Post post = Post.builder()
                .user(user)
                .gym(user.getGym())
                .title(requestDTO.getTitle())
                .postType(requestDTO.getPostType())
                .content(requestDTO.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        postRepository.save(post);

        return PostResponseDTO.from(post);
    }

    @Transactional
    public PostResponseDTO updatePost(Long userId, Long postId, PostUpdateDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isManager()) {
            throw new PostAccessDeniedException();
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        post.validateWriter(userId);
        post.updatePost(request, LocalDateTime.now());

        return PostResponseDTO.from(post);
    }

    public PostResponseDTO getPostById(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(PostNotFoundException::new);

        return PostResponseDTO.from(post);
    }

    public Page<PostSummaryDTO> getAllByGym(Long gymId, Pageable pageable) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

        return postRepository.findAllByGym(gym, pageable).map(PostSummaryDTO::from);
    }

    public Page<PostSummaryDTO> getAllByGymWithPostType(Long gymId, PostType postType, Pageable pageable) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

        return postRepository.findAllByGymAndPostType(gym, postType, pageable).map(PostSummaryDTO::from);
    }
}
