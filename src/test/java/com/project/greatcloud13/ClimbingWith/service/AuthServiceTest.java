package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.LoginRequest;
import com.project.greatcloud13.ClimbingWith.dto.LoginResponse;
import com.project.greatcloud13.ClimbingWith.dto.SignUpRequest;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Role;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.security.JwtTokenProvider;
import com.project.greatcloud13.ClimbingWith.util.EntityFixture;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
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
    3.1 [Given] 테스트에 필요한 파라미터, Mock 동작 정의
    3.2 [When] 테스트에 대한 상황을 정의
    3.3 [Then] 동작 이후 결과를 검증
    3.4 상황과 동시에 동작 결과가 발생하는 경우 [When & Then]을 통해 정의
4. verify를 통한 메소드의 동작 여부 또한 검증
 */

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtTokenProvider jwtTokenProvider;

//  ========================= Mock Objects =========================
    private User mockMember;
    private User mockGymManager;
    private Gym mockGym;
//  ===============================================================

//  ==================== Mock Object parameter ====================
    Long memberId = 1L;
    String memberUsername = "testuser";
    String memberNickname = "테스터";
    String memberEmail = "test@test.com";
    String rawPassword = "password123";
    String encodedPassword = "$2a$10$encodedPasswordHash";

    Long managerId = 2L;
    String managerUsername = "gymmanager";
    Long gymId = 10L;
    String gymName = "테스트 암장";

    Long invalidUserId = 999L;
    String invalidUsername = "nobody";
//  ===============================================================

    @BeforeEach
    void setUp() {
        mockGym = EntityFixture.createGym(gymId, gymName);

        mockMember = EntityFixture.createMember(memberId, memberUsername, memberNickname);
        ReflectionTestUtils.setField(mockMember, "password", encodedPassword);

        mockGymManager = EntityFixture.createGymManager(managerId, managerUsername, mockGym);
        ReflectionTestUtils.setField(mockGymManager, "password", encodedPassword);
    }

    @Nested
    @DisplayName("signup() 기능 테스트")
    class SignUpTest {

        @Test
        @DisplayName("회원가입 성공")
        void signup_Success() {
            // [Given]
            SignUpRequest request = new SignUpRequest(memberUsername, memberNickname, memberEmail, rawPassword);

            given(userRepository.existsByUsername(memberUsername)).willReturn(false);
            given(userRepository.existsByNickname(memberNickname)).willReturn(false);
            given(userRepository.existsByEmail(memberEmail)).willReturn(false);
            given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);

            // [When]
            authService.signup(request);

            // [Then]
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("실패: 이미 사용중인 아이디로 가입 시 예외 발생")
        void signup_DuplicateUsername() {
            // [Given]
            SignUpRequest request = new SignUpRequest(memberUsername, memberNickname, memberEmail, rawPassword);

            given(userRepository.existsByUsername(memberUsername)).willReturn(true);

            // [When & Then]
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 사용중인 아이디입니다.");

            verify(userRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("실패: 이미 사용중인 닉네임으로 가입 시 예외 발생")
        void signup_DuplicateNickname() {
            // [Given]
            SignUpRequest request = new SignUpRequest(memberUsername, memberNickname, memberEmail, rawPassword);

            given(userRepository.existsByUsername(memberUsername)).willReturn(false);
            given(userRepository.existsByNickname(memberNickname)).willReturn(true);

            // [When & Then]
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 사용중인 닉네임입니다.");

            verify(userRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("실패: 이미 사용중인 이메일로 가입 시 예외 발생")
        void signup_DuplicateEmail() {
            // [Given]
            SignUpRequest request = new SignUpRequest(memberUsername, memberNickname, memberEmail, rawPassword);

            given(userRepository.existsByUsername(memberUsername)).willReturn(false);
            given(userRepository.existsByNickname(memberNickname)).willReturn(false);
            given(userRepository.existsByEmail(memberEmail)).willReturn(true);

            // [When & Then]
            assertThatThrownBy(() -> authService.signup(request))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("이미 사용중인 이메일 입니다.");

            verify(userRepository, times(0)).save(any());
        }

        @Test
        @DisplayName("비밀번호가 평문이 아닌 인코딩되어 저장되는지 검증")
        void signup_PasswordEncoded() {
            // [Given]
            SignUpRequest request = new SignUpRequest(memberUsername, memberNickname, memberEmail, rawPassword);

            given(userRepository.existsByUsername(memberUsername)).willReturn(false);
            given(userRepository.existsByNickname(memberNickname)).willReturn(false);
            given(userRepository.existsByEmail(memberEmail)).willReturn(false);
            given(passwordEncoder.encode(rawPassword)).willReturn(encodedPassword);

            // [When]
            authService.signup(request);

            // [Then] — 인코더가 호출되었고, save된 User의 password는 평문이 아님
            verify(passwordEncoder, times(1)).encode(rawPassword);
            verify(userRepository).save(argThat(user ->
                    !user.getPassword().equals(rawPassword) &&
                    user.getPassword().equals(encodedPassword)
            ));
        }
    }

    @Nested
    @DisplayName("login() 기능 테스트")
    class LoginTest {

        @Test
        @DisplayName("일반 회원 로그인 성공 — managedGymId는 null")
        void login_Member_Success() {
            // [Given]
            LoginRequest request = new LoginRequest(memberUsername, rawPassword);

            Authentication mockAuth = mock(Authentication.class);
            given(mockAuth.getName()).willReturn(memberUsername);
            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(mockAuth);
            given(userRepository.findByUsername(memberUsername)).willReturn(Optional.of(mockMember));
            given(jwtTokenProvider.generateToken(memberUsername)).willReturn("mock.jwt.token");

            // [When]
            LoginResponse response = authService.login(request);

            // [Then]
            assertThat(response.getToken()).isEqualTo("mock.jwt.token");
            assertThat(response.getUsername()).isEqualTo(memberUsername);
            assertThat(response.getRole()).isEqualTo(Role.MEMBER.toString());
            assertThat(response.getManagedGymId()).isNull();
            assertThat(response.getNickname()).isEqualTo(memberNickname);
        }

        @Test
        @DisplayName("암장 매니저 로그인 성공 — managedGymId가 포함됨")
        void login_GymManager_Success() {
            // [Given]
            LoginRequest request = new LoginRequest(managerUsername, rawPassword);

            Authentication mockAuth = mock(Authentication.class);
            given(mockAuth.getName()).willReturn(managerUsername);
            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willReturn(mockAuth);
            given(userRepository.findByUsername(managerUsername)).willReturn(Optional.of(mockGymManager));
            given(jwtTokenProvider.generateToken(managerUsername)).willReturn("mock.jwt.token");

            // [When]
            LoginResponse response = authService.login(request);

            // [Then]
            assertThat(response.getRole()).isEqualTo(Role.GYM_MANAGER.toString());
            assertThat(response.getManagedGymId()).isEqualTo(gymId);
        }

        @Test
        @DisplayName("실패: 잘못된 비밀번호로 로그인 시 예외 발생")
        void login_WrongPassword() {
            // [Given]
            LoginRequest request = new LoginRequest(memberUsername, "wrongpassword");

            given(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                    .willThrow(new BadCredentialsException("자격 증명에 실패하였습니다."));

            // [When & Then]
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(BadCredentialsException.class);

            verify(jwtTokenProvider, times(0)).generateToken(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자로 로그인 시 예외 발생")
        void login_UserNotFound() {
            // [Given]
            LoginRequest request = new LoginRequest(invalidUsername, rawPassword);

            Authentication mockAuth = mock(Authentication.class);
            given(mockAuth.getName()).willReturn(invalidUsername);
            given(authenticationManager.authenticate(any())).willReturn(mockAuth);
            given(userRepository.findByUsername(invalidUsername)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> authService.login(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }
    }

    @Nested
    @DisplayName("withdraw() 기능 테스트")
    class WithdrawTest {

        @Test
        @DisplayName("회원탈퇴 성공 — isActive가 false로 변경됨")
        void withdraw_Success() {
            // [Given]
            LoginRequest request = new LoginRequest(memberUsername, rawPassword);

            given(userRepository.findByUsername(memberUsername)).willReturn(Optional.of(mockMember));
            // checkPassword 내부: passwordEncoder.matches(rawPassword, encodedPassword) → true
            given(passwordEncoder.matches(rawPassword, encodedPassword)).willReturn(true);

            // [When]
            authService.withdraw(request);

            // [Then]
            assertThat(mockMember.isActive()).isFalse();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 요청 시 예외 발생")
        void withdraw_UserNotFound() {
            // [Given]
            LoginRequest request = new LoginRequest(invalidUsername, rawPassword);

            given(userRepository.findByUsername(invalidUsername)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> authService.withdraw(request))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");
        }

        @Test
        @DisplayName("실패: 비밀번호가 틀릴 경우 예외 발생 — [버그] checkPassword 이중 해시로 인해 현재 항상 실패")
        void withdraw_WrongPassword() {
            // [Given]
            LoginRequest request = new LoginRequest(memberUsername, "wrongpassword");

            given(userRepository.findByUsername(memberUsername)).willReturn(Optional.of(mockMember));
            // checkPassword 내부: passwordEncoder.matches(rawPassword, encodedPassword) → false
            given(passwordEncoder.matches("wrongpassword", encodedPassword)).willReturn(false);

            // [When & Then]
            // 현재 User.checkPassword()는 passwordEncoder.encode(password)를 인자로 넘기는 버그가 있어
            // 이 Mock이 호출되지 않고 AccessDeniedException이 발생하거나 정상 통과될 수 있음.
            // 버그 수정(encode 제거) 후 이 테스트가 올바르게 동작해야 함.
            assertThatThrownBy(() -> authService.withdraw(request))
                    .isInstanceOf(AccessDeniedException.class)
                    .hasMessage("계정 정보가 올바르지 않습니다");
        }
    }
}
