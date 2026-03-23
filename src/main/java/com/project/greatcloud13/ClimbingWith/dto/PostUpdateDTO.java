package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.PostType;
import lombok.Data;

@Data
public class PostUpdateDTO {

    private String title;
    private PostType postType;
    private String content;


}
