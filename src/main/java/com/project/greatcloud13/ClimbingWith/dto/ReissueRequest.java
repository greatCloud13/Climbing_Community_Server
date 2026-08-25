package com.project.greatcloud13.ClimbingWith.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ReissueRequest {

    @NotBlank(message = "리프레시 토큰은 필수입니다.")
    private String refreshToken;

}
