package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.LoginRequest;
import com.project.greatcloud13.ClimbingWith.dto.LoginResponse;
import com.project.greatcloud13.ClimbingWith.dto.SignUpRequest;
import com.project.greatcloud13.ClimbingWith.dto.TokenResponse;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Role;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.auth.DuplicateFieldException;
import com.project.greatcloud13.ClimbingWith.exception.auth.InvalidRefreshTokenException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.security.JwtTokenProvider;
import com.project.greatcloud13.ClimbingWith.security.RefreshTokenService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/*==================테스트 코드 작성 규칙===========================
1. 테스트 대상 Class 내부 메소드 외의 의존성은 Mock 주입을 통해 동작 정의
2. 테스트에 사용되는 중복되는 파라미터 및 Entity는 클래스 영역에 정의
3. 테스트 코드 작성 양식은 [Given], [When], [Then] 양식으로 작성
4. verify를 통한 메소드의 동작 여부 또한 검증
 */

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private AuthenticationManager authenticationManager;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private RefreshTokenService refreshTokenService;

//   ========================= Mock Objects =========================
    private User mockMember;
    private User mockManager;
    private Gym mockGym;
//    ===============================================================

    Long gymId = 100L;
    Long userId = 500L;
    String username = "tester1";
    String password = "password1";
    String encodedPassword = "encoded_password";
    String token = "jwt.token.value";
    String refreshToken = "jwt.refresh.token.value";

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").build();
        ReflectionTestUtils.setField(mockGym, "id", gymId);

        mockMember = User.builder()
                .username(username)
                .email("tester1@test.com")
                .password(encodedPassword)
                .nickname("닉네임1")
                .build();
        ReflectionTestUtils.setField(mockMember, "id", userId);

        mockManager = User.builder()
                .username("manager1")
                .email("manager1@test.com")
                .password(encodedPassword)
                .nickname("매니저1")
                .build();
        ReflectionTestUtils.setField(mockManager, "id", 501L);
        ReflectionTestUtils.setField(mockManager, "role", Role.GYM_MANAGER);
        ReflectionTestUtils.setField(mockManager, "gym", mockGym);
    }

    @Nested
    @DisplayName("signup() 메서드 테스트")
    class SignupTest {

        @Test
        @DisplayName("정상 회원가입 성공")
        void signup_success() {
            // [Given]
            SignUpRequest request = new SignUpRequest(username, "닉네임1", "tester1@test.com", password);
            given(userRepository.existsByUsername(username)).willReturn(false);
            given(userRepository.existsByNickname("닉네임1")).willReturn(false);
            given(userRepository.existsByEmail("tester1@test.com")).willReturn(false);
            given(passwordEncoder.encode(password)).willReturn(encodedPassword);

            // [When]
            authService.signup(request);

            // [Then]
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("중복 username → DuplicateFieldException")
        void signup_duplicateUsername() {
            // [Given]
            SignUpRequest request = new SignUpRequest(username, "닉네임1", "tester1@test.com", password);
            given(userRepository.existsByUsername(username)).willReturn(true);

            // [When] & [Then]
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(DuplicateFieldException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("중복 nickname → DuplicateFieldException")
        void signup_duplicateNickname() {
            // [Given]
            SignUpRequest request = new SignUpRequest(username, "닉네임1", "tester1@test.com", password);
            given(userRepository.existsByUsername(username)).willReturn(false);
            given(userRepository.existsByNickname("닉네임1")).willReturn(true);

            // [When] & [Then]
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(DuplicateFieldException.class);

            verify(userRepository, never()).save(any());
        }

        @Test
        @DisplayName("중복 email → DuplicateFieldException")
        void signup_duplicateEmail() {
            // [Given]
            SignUpRequest request = new SignUpRequest(username, "닉네임1", "tester1@test.com", password);
            given(userRepository.existsByUsername(username)).willReturn(false);
            given(userRepository.existsByNickname("닉네임1")).willReturn(false);
            given(userRepository.existsByEmail("tester1@test.com")).willReturn(true);

            // [When] & [Then]
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(DuplicateFieldException.class);

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("login() 메서드 테스트")
    class LoginTest {

        @Test
        @DisplayName("일반 MEMBER 로그인 성공 - managedGymId null 반환")
        void login_member_success() {
            // [Given]
            LoginRequest request = new LoginRequest(username, password);
            Authentication mockAuth = mock(Authentication.class);
            given(mockAuth.getName()).willReturn(username);
            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(mockAuth);
            given(jwtTokenProvider.generateAccessToken(username)).willReturn(token);
            given(jwtTokenProvider.generateRefreshToken(username)).willReturn(refreshToken);
            given(userRepository.findByUsername(username)).willReturn(Optional.of(mockMember));

            // [When]
            LoginResponse response = authService.login(request);

            // [Then]
            assertThat(response.getToken()).isEqualTo(token);
            assertThat(response.getRefreshToken()).isEqualTo(refreshToken);
            assertThat(response.getUsername()).isEqualTo(username);
            assertThat(response.getRole()).isEqualTo(Role.MEMBER.toString());
            assertThat(response.getManagedGymId()).isNull();
            assertThat(response.getNickname()).isEqualTo("닉네임1");
            verify(refreshTokenService, times(1)).save(username, refreshToken);
        }

        @Test
        @DisplayName("GYM_MANAGER 로그인 성공 - managedGymId 반환")
        void login_gymManager_success() {
            // [Given]
            LoginRequest request = new LoginRequest("manager1", password);
            Authentication mockAuth = mock(Authentication.class);
            given(mockAuth.getName()).willReturn("manager1");
            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(mockAuth);
            given(jwtTokenProvider.generateAccessToken("manager1")).willReturn(token);
            given(jwtTokenProvider.generateRefreshToken("manager1")).willReturn(refreshToken);
            given(userRepository.findByUsername("manager1")).willReturn(Optional.of(mockManager));

            // [When]
            LoginResponse response = authService.login(request);

            // [Then]
            assertThat(response.getRole()).isEqualTo(Role.GYM_MANAGER.toString());
            assertThat(response.getManagedGymId()).isEqualTo(gymId);
        }

        @Test
        @DisplayName("인증 후 DB에 없는 username → UserNotFoundException")
        void login_userNotFound() {
            // [Given]
            LoginRequest request = new LoginRequest(username, password);
            Authentication mockAuth = mock(Authentication.class);
            given(mockAuth.getName()).willReturn(username);
            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(mockAuth);
            given(jwtTokenProvider.generateAccessToken(username)).willReturn(token);
            given(jwtTokenProvider.generateRefreshToken(username)).willReturn(refreshToken);
            given(userRepository.findByUsername(username)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("reissue() 메서드 테스트")
    class ReissueTest {

        String newToken = "jwt.new.token.value";
        String newRefreshToken = "jwt.new.refresh.token.value";

        @Test
        @DisplayName("정상 재발급 성공 - 새 토큰 쌍 반환 및 Redis 값 회전")
        void reissue_success() {
            // [Given]
            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.getTokenType(refreshToken)).willReturn(JwtTokenProvider.TOKEN_TYPE_REFRESH);
            given(jwtTokenProvider.getUsernameFormToken(refreshToken)).willReturn(username);
            given(refreshTokenService.matches(username, refreshToken)).willReturn(true);
            given(jwtTokenProvider.generateAccessToken(username)).willReturn(newToken);
            given(jwtTokenProvider.generateRefreshToken(username)).willReturn(newRefreshToken);

            // [When]
            TokenResponse response = authService.reissue(refreshToken);

            // [Then]
            assertThat(response.getToken()).isEqualTo(newToken);
            assertThat(response.getRefreshToken()).isEqualTo(newRefreshToken);
            verify(refreshTokenService, times(1)).save(username, newRefreshToken);
        }

        @Test
        @DisplayName("만료/위조된 토큰 → InvalidRefreshTokenException")
        void reissue_invalidToken() {
            // [Given]
            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(false);

            // [When] & [Then]
            assertThatThrownBy(() -> authService.reissue(refreshToken))
                    .isInstanceOf(InvalidRefreshTokenException.class);
            verify(refreshTokenService, never()).save(any(), any());
        }

        @Test
        @DisplayName("Access Token으로 재발급 시도 → InvalidRefreshTokenException")
        void reissue_wrongTokenType() {
            // [Given]
            given(jwtTokenProvider.validateToken(token)).willReturn(true);
            given(jwtTokenProvider.getTokenType(token)).willReturn(JwtTokenProvider.TOKEN_TYPE_ACCESS);

            // [When] & [Then]
            assertThatThrownBy(() -> authService.reissue(token))
                    .isInstanceOf(InvalidRefreshTokenException.class);
        }

        @Test
        @DisplayName("Redis에 저장된 값과 불일치(재사용 의심) → InvalidRefreshTokenException, 세션 무효화")
        void reissue_tokenMismatch() {
            // [Given]
            given(jwtTokenProvider.validateToken(refreshToken)).willReturn(true);
            given(jwtTokenProvider.getTokenType(refreshToken)).willReturn(JwtTokenProvider.TOKEN_TYPE_REFRESH);
            given(jwtTokenProvider.getUsernameFormToken(refreshToken)).willReturn(username);
            given(refreshTokenService.matches(username, refreshToken)).willReturn(false);

            // [When] & [Then]
            assertThatThrownBy(() -> authService.reissue(refreshToken))
                    .isInstanceOf(InvalidRefreshTokenException.class);
            verify(refreshTokenService, times(1)).delete(username);
        }
    }

    @Nested
    @DisplayName("logout() 메서드 테스트")
    class LogoutTest {

        @Test
        @DisplayName("로그아웃 시 Refresh Token 삭제")
        void logout_success() {
            // [When]
            authService.logout(username);

            // [Then]
            verify(refreshTokenService, times(1)).delete(username);
        }
    }

    @Nested
    @DisplayName("withdraw() 메서드 테스트")
    class WithdrawTest {

        @Test
        @DisplayName("정상 탈퇴 성공 - isActive false로 변경")
        void withdraw_success() {
            // [Given]
            LoginRequest request = new LoginRequest(username, password);
            given(userRepository.findByUsername(username)).willReturn(Optional.of(mockMember));
            given(passwordEncoder.matches(password, encodedPassword)).willReturn(true);

            // [When]
            authService.withdraw(request);

            // [Then]
            assertThat(mockMember.isActive()).isFalse();
            verify(refreshTokenService, times(1)).delete(username);
        }

        @Test
        @DisplayName("존재하지 않는 username → UserNotFoundException")
        void withdraw_userNotFound() {
            // [Given]
            LoginRequest request = new LoginRequest(username, password);
            given(userRepository.findByUsername(username)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> authService.withdraw(request))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("비밀번호 불일치 → AccessDeniedException")
        void withdraw_wrongPassword() {
            // [Given]
            LoginRequest request = new LoginRequest(username, "wrongPassword");
            given(userRepository.findByUsername(username)).willReturn(Optional.of(mockMember));
            given(passwordEncoder.matches("wrongPassword", encodedPassword)).willReturn(false);

            // [When] & [Then]
            assertThatThrownBy(() -> authService.withdraw(request))
                    .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
        }
    }
}
