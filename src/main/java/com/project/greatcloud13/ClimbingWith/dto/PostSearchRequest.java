package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.PostType;
import lombok.Data;

@Data
public class PostSearchRequest {

    String query;

    Integer count;

    Long gymId;

    PostType postType;
}
