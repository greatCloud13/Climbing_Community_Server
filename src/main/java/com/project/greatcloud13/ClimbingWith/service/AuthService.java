package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.dto.LoginRequest;
import com.project.greatcloud13.ClimbingWith.dto.LoginResponse;
import com.project.greatcloud13.ClimbingWith.dto.SignUpRequest;
import com.project.greatcloud13.ClimbingWith.dto.TokenResponse;
import com.project.greatcloud13.ClimbingWith.entity.Role;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.auth.DuplicateFieldException;
import com.project.greatcloud13.ClimbingWith.exception.auth.InvalidRefreshTokenException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.security.JwtTokenProvider;
import com.project.greatcloud13.ClimbingWith.security.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

    public void signup(SignUpRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateFieldException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (userRepository.existsByNickname(request.getNickname())) {
            throw new DuplicateFieldException(ErrorCode.DUPLICATE_NICKNAME);
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateFieldException(ErrorCode.DUPLICATE_EMAIL);
        }

        User user = User.builder()
                .username(request.getUsername())
                .nickname(request.getNickname())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(
                        request.getUsername(),
                        request.getPassword()
                );

        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        String username = authentication.getName();
        String token = jwtTokenProvider.generateAccessToken(username);
        String refreshToken = jwtTokenProvider.generateRefreshToken(username);
        refreshTokenService.save(username, refreshToken);

        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return new LoginResponse(user.getId(), token, refreshToken, username, user.getRole().toString(),
                user.getRole() == Role.GYM_MANAGER ? user.getGym().getId() : null,
                user.getNickname());
    }

    public TokenResponse reissue(String refreshToken) {
        if (!jwtTokenProvider.validateToken(refreshToken)
                || !JwtTokenProvider.TOKEN_TYPE_REFRESH.equals(jwtTokenProvider.getTokenType(refreshToken))) {
            throw new InvalidRefreshTokenException();
        }

        String username = jwtTokenProvider.getUsernameFormToken(refreshToken);

        if (!refreshTokenService.matches(username, refreshToken)) {
            // 저장된 값과 다른 refresh token이 제시됨(이미 회전되어 폐기된 토큰 재사용 등 탈취 의심) → 해당 사용자의 세션을 모두 무효화
            refreshTokenService.delete(username);
            throw new InvalidRefreshTokenException();
        }

        String newAccessToken = jwtTokenProvider.generateAccessToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);
        refreshTokenService.save(username, newRefreshToken);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String username) {
        refreshTokenService.delete(username);
    }

    public void withdraw(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(UserNotFoundException::new);

        user.checkPassword(request.getPassword(), passwordEncoder);
        user.deactivate();
        refreshTokenService.delete(user.getUsername());
    }
}
