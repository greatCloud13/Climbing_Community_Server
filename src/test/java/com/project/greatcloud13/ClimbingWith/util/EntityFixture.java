package com.project.greatcloud13.ClimbingWith.util;

import com.project.greatcloud13.ClimbingWith.entity.*;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;

/**
 * 테스트용 엔티티 팩토리 — 모든 Mock 엔티티 생성은 여기서
 */
public class EntityFixture {

    public static Gym createGym(Long id, String name) {
        Gym gym = Gym.builder().gymName(name).build();
        ReflectionTestUtils.setField(gym, "id", id);
        return gym;
    }

    public static User createMember(Long id, String username, String nickname) {
        User user = User.builder()
                .username(username)
                .nickname(nickname)
                .email(username + "@test.com")
                .password("encoded_password")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "role", Role.MEMBER);
        return user;
    }

    public static User createGymManager(Long id, String username, Gym gym) {
        User user = User.builder()
                .username(username)
                .nickname(username + "_nick")
                .email(username + "@test.com")
                .password("encoded_password")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "role", Role.GYM_MANAGER);
        ReflectionTestUtils.setField(user, "gym", gym);
        return user;
    }

    public static User createAdmin(Long id, String username) {
        User user = User.builder()
                .username(username)
                .nickname(username + "_nick")
                .email(username + "@test.com")
                .password("encoded_password")
                .build();
        ReflectionTestUtils.setField(user, "id", id);
        ReflectionTestUtils.setField(user, "role", Role.ADMIN);
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
        ReflectionTestUtils.setField(post, "writer", writer);
        return post;
    }
}
