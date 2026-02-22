package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.User;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDTO {

    private Long id;

    private String username;

    private String email;

    private String role;

    private String gymName;

    private String nickname;

    public static UserDTO from(User user){
        return UserDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().toString())
                .gymName(user.getGym() != null ? user.getGym().getGymName() : null)
                .nickname(user.getNickname())
                .build();

    }
}
