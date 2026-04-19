package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Post;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostSearchResultSummaryDTO {

    private Long id;

    private String postTitle;

    private String postedAt;


    public static PostSearchResultSummaryDTO from(Post post){
        return PostSearchResultSummaryDTO.builder()
                .id(post.getId())
                .postTitle(post.getTitle())
                .postedAt(post.getContent())
                .build();
    }
}
