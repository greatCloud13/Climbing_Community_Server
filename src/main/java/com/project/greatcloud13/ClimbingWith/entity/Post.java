package com.project.greatcloud13.ClimbingWith.entity;

import com.project.greatcloud13.ClimbingWith.dto.PostUpdateDTO;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "post")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User writer;

    private String title;

    @Enumerated(EnumType.STRING)
    private PostType postType;

    private String content;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public Post(Gym gym, User user, String title, PostType postType, String content, LocalDateTime createdAt){
        this.gym=gym;
        this.writer = user;
        this.title = title;
        this.postType = postType;
        this.content = content;
        this.createdAt = createdAt;
    }

    public void validateWriter(Long accessUserId){
        if(!this.getWriter().getId().equals(accessUserId)){
            throw new IllegalArgumentException("작성자만 게시글을 수정할 수 있습니다.");
        }
    }

    public void updatePost(PostUpdateDTO request, LocalDateTime updatedAt){
        if(request.getTitle() == null || request.getTitle().isBlank()){
            throw new IllegalArgumentException("제목이 유효하지 않습니다.");
        }
        if(request.getContent() == null || request.getContent().isBlank()){
            throw new IllegalArgumentException("내용이 유효하지 않습니다.");
        }
        this.title = request.getTitle();
        this.content = request.getContent();
        this.postType = request.getPostType();
        this.updatedAt = updatedAt;
    }
}
