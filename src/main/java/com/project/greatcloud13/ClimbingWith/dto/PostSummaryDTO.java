package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSummaryDTO {

    private Long id;
    private String title;
    private PostType postType;
    private String createdAt;

    public static PostSummaryDTO from(Post post){
        return PostSummaryDTO.builder()
                .id(post.getId())
                .title(post.getTitle())
                .postType(post.getPostType())
                .createdAt(post.getCreatedAt().toString())
                .build();
    }


}
