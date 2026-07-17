package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostMessage {

    private String gymName;
    private String postTitle;
    private Long postId;
    private Long gymId;
    private PostType postType;
    private String content;


    public static PostMessage from(Post post){
        return new PostMessage(post.getGym().getGymName(), post.getTitle(), post.getId(), post.getGym().getId(), post.getPostType(), post.getContent());
    }
}