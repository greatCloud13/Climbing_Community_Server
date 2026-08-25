package com.project.greatcloud13.ClimbingWith.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenResponse {

    private String token;

    private String refreshToken;

}
