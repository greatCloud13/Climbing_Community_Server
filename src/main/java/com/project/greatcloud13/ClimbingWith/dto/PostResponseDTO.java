package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.PostType;
import lombok.Data;

@Data
public class PostResponseDTO {

    private String title;
    private String gymName;
    private String writer;
    private PostType postType;
    private String content;
    private String createdAt;
    private String updatedAt;
}
