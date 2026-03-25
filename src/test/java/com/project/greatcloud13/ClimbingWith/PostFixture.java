package com.project.greatcloud13.ClimbingWith;

import com.project.greatcloud13.ClimbingWith.entity.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

public class PostFixture {

    public static Gym createGym(Long id, String name) {
        Gym gym = Gym.builder().gymName(name).build();
        ReflectionTestUtils.setField(gym, "id", id);
        return gym;
    }

    public static User createUser(Long id, String username, Role role, Gym gym) {
        User user = User.builder().username(username).build();
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "role", role);
        if (gym != null) {
            ReflectionTestUtils.setField(user, "gym", gym);
        }
        return user;
    }

    public static Post createPost(Long id, String title, String content, PostType type, Gym gym, User writer, LocalDateTime createdAt) {
        Post post = Post.builder()
                .gym(gym)
                .user(writer)
                .title(title)
                .postType(type)
                .content(content)
                .createdAt(createdAt)
                .build();
        ReflectionTestUtils.setField(post, "id", id);
        ReflectionTestUtils.setField(post, "writer", writer); // 빌더와 별개로 필드 주입 보장
        return post;
    }
}