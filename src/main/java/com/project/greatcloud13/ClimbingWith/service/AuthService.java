package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.LoginRequest;
import com.project.greatcloud13.ClimbingWith.dto.LoginResponse;
import com.project.greatcloud13.ClimbingWith.dto.SignUpRequest;
import com.project.greatcloud13.ClimbingWith.entity.Role;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.security.JwtTokenProvider;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public void signup(SignUpRequest request){

        if(userRepository.existsByUsername(request.getUsername())){
            throw new IllegalArgumentException("이미 사용중인 아이디입니다.");
        }
        if(userRepository.existsByNickname(request.getNickname())) {
            throw new IllegalArgumentException("이미 사용중인 닉네임입니다.");
        }
        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("이미 사용중인 이메일 입니다.");

        }

        User user = User.builder()
                .username(request.getUsername())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    @Transactional
    public LoginResponse login(LoginRequest request){

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String username = authentication.getName();
        String token = jwtTokenProvider.generateToken(username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return new LoginResponse(token, username, user.getRole().toString(), user.getRole()==Role.GYM_MANAGER ? user.getGym().getId() : null , user.getNickname());
    }

    @Transactional
    public void withdraw(LoginRequest request){

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        user.checkPassword(request.getPassword(), passwordEncoder);
        user.deactivate();
    }
}
