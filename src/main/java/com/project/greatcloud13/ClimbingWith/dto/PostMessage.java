package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostMessage implements Serializable {

    private Long postId;
    private String content;


    public static PostMessage from(Post post){
        return new PostMessage(post.getId(), post.getContent());
    }
}
