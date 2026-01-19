package com.project.greatcloud13.ClimbingWith.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank(message = "사용자 아이디는 필수업니다.")
    @Size(min = 4, max = 20, message = "아이디는 4~20자여야 합니다.")
    private String username;

    @NotBlank(message = "닉네임은 필수업니다.")
    @Size(min = 4, max = 20, message = "닉네임은 3~10자여야 합니다.")
    private String nickname;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바르지 않은 이메일 형식입니다.")
    private String email;

    private String password;

}
