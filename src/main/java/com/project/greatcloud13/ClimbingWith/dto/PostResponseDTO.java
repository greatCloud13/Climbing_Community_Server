package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostResponseDTO {

    private String title;
    private String gymName;
    private String writer;
    private String postType;
    private String content;
    private String createdAt;
    private String updatedAt;

    public static PostResponseDTO from(Post post){
        return PostResponseDTO.builder()
                .title(post.getTitle())
                .gymName(post.getGym().getGymName())
                .writer(post.getWriter().getUsername())
                .postType(post.getPostType().toString())
                .content(post.getContent())
                .createdAt(post.getCreatedAt().toString())
                .updatedAt(post.getUpdatedAt()!=null ? post.getUpdatedAt().toString() : null)
                .build();
    }
}
