package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.AuthResponse;
import com.project.greatcloud13.ClimbingWith.dto.LoginRequest;
import com.project.greatcloud13.ClimbingWith.dto.LoginResponse;
import com.project.greatcloud13.ClimbingWith.dto.SignUpRequest;
import com.project.greatcloud13.ClimbingWith.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<AuthResponse> signup(@RequestBody SignUpRequest request){
        authService.signup(request);

        return ResponseEntity.ok(new AuthResponse("가입이 완료되었습니다."));
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request){
        LoginResponse result = authService.login(request);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/withdraw")
    public ResponseEntity<AuthResponse> withdraw(@RequestBody LoginRequest request){
        authService.withdraw(request);

        return ResponseEntity.ok(new AuthResponse("탈퇴가 완료되었습니다."));
    }

}
