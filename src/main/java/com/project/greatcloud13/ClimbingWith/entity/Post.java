package com.project.greatcloud13.ClimbingWith.entity;

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
}
