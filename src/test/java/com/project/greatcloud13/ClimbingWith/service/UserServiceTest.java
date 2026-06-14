package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.common.SearchTag;
import com.project.greatcloud13.ClimbingWith.dto.UserDTO;
import com.project.greatcloud13.ClimbingWith.dto.UserDetailDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Role;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import jakarta.persistence.EntityNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.util.TestFixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock private UserRepository userRepository;
    @Mock private GymRepository gymRepository;

//   ========================= Mock Objects =========================
    private Gym mockGym;
    private User mockUser;
    private User mockManager;
//    ===============================================================

    Long gymId = 100L;
    Long userId = 500L;
    Long managerId = 501L;
    Long invalidId = 999L;
    String username = "tester1";
    String managerUsername = "manager1";

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").build();
        ReflectionTestUtils.setField(mockGym, "id", gymId);

        mockUser = User.builder().username(username).email("t1@test.com").password("pw").nickname("닉네임1").build();
        ReflectionTestUtils.setField(mockUser, "id", userId);
        ReflectionTestUtils.setField(mockUser, "role", Role.MEMBER);

        mockManager = User.builder().username(managerUsername).email("m@test.com").password("pw").nickname("매니저").build();
        ReflectionTestUtils.setField(mockManager, "id", managerId);
        ReflectionTestUtils.setField(mockManager, "role", Role.GYM_MANAGER);
        ReflectionTestUtils.setField(mockManager, "gym", mockGym);
    }

    @Nested
    @DisplayName("getUserList() 메서드 테스트")
    class GetUserListTest {

        @Test
        @DisplayName("사용자 목록 페이지 조회 성공")
        void getUserList_Success() {
            // [Given]
            int page = 0, size = 10;
            PageRequest pageable = PageRequest.of(page, size, Sort.by("id").descending());
            Page<User> mockPage = TestFixture.createMockPage(
                    () -> User.builder().username("user").email("u@test.com").password("pw").build(), size);

            given(userRepository.findAll(pageable)).willReturn(mockPage);

            // [When]
            Page<UserDTO> result = userService.getUserList(page, size);

            // [Then]
            assertThat(result.getSize()).isEqualTo(size);
            verify(userRepository, times(1)).findAll(pageable);
        }
    }

    @Nested
    @DisplayName("searchUser() 메서드 테스트")
    class SearchUserTest {

        @Test
        @DisplayName("USERNAME으로 사용자 검색 성공")
        void searchUser_ByUsername_Success() {
            // [Given]
            String keyword = "tester";
            PageRequest pageable = PageRequest.of(0, 100);
            Page<User> mockPage = TestFixture.createMockPage(
                    () -> User.builder().username(keyword).email("t@test.com").password("pw").build(), 1);

            given(userRepository.findAllByUsername(keyword, pageable)).willReturn(mockPage);

            // [When]
            Page<UserDTO> result = userService.searchUser(keyword, SearchTag.USERNAME, 0);

            // [Then]
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(userRepository, times(1)).findAllByUsername(keyword, pageable);
            verify(userRepository, never()).findAllByEmail(any(), any());
            verify(userRepository, never()).findAllByNickname(any(), any());
        }

        @Test
        @DisplayName("EMAIL로 사용자 검색 성공")
        void searchUser_ByEmail_Success() {
            // [Given]
            String keyword = "test@test.com";
            PageRequest pageable = PageRequest.of(0, 100);
            Page<User> mockPage = TestFixture.createMockPage(
                    () -> User.builder().username("user").email(keyword).password("pw").build(), 1);

            given(userRepository.findAllByEmail(keyword, pageable)).willReturn(mockPage);

            // [When]
            Page<UserDTO> result = userService.searchUser(keyword, SearchTag.EMAIL, 0);

            // [Then]
            assertThat(result.getTotalElements()).isEqualTo(1);
            verify(userRepository, times(1)).findAllByEmail(keyword, pageable);
        }

        @Test
        @DisplayName("NICKNAME으로 사용자 검색 성공")
        void searchUser_ByNickname_Success() {
            // [Given]
            String keyword = "닉네임";
            PageRequest pageable = PageRequest.of(0, 100);
            Page<User> mockPage = TestFixture.createMockPage(
                    () -> User.builder().username("user").email("u@test.com").password("pw").build(), 2);

            given(userRepository.findAllByNickname(keyword, pageable)).willReturn(mockPage);

            // [When]
            Page<UserDTO> result = userService.searchUser(keyword, SearchTag.NICKNAME, 0);

            // [Then]
            assertThat(result.getTotalElements()).isEqualTo(2);
            verify(userRepository, times(1)).findAllByNickname(keyword, pageable);
        }
    }

    @Nested
    @DisplayName("getUserDetail() 메서드 테스트")
    class GetUserDetailTest {

        @Test
        @DisplayName("사용자 상세 조회 성공")
        void getUserDetail_Success() {
            // [Given]
            given(userRepository.findByUsername(username)).willReturn(Optional.of(mockUser));

            // [When]
            UserDetailDTO result = userService.getUserDetail(username);

            // [Then]
            assertThat(result.getUsername()).isEqualTo(username);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 username 요청시 예외 발생")
        void getUserDetail_UserNotFound() {
            // [Given]
            given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> userService.getUserDetail("unknown"))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("assignGymManager() 메서드 테스트")
    class AssignGymManagerTest {

        @Test
        @DisplayName("암장 관리자 지정 성공")
        void assignGymManager_Success() {
            // [Given]
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));

            // [When]
            UserDetailDTO result = userService.assignGymManager(gymId, userId);

            // [Then]
            assertThat(result.getUsername()).isEqualTo(username);
            // 역할이 GYM_MANAGER로 변경되었는지 확인
            assertThat(mockUser.getRole()).isEqualTo(Role.GYM_MANAGER);
            assertThat(mockUser.getGym()).isEqualTo(mockGym);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 암장 ID 요청시 예외 발생")
        void assignGymManager_GymNotFound() {
            // [Given]
            given(gymRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> userService.assignGymManager(invalidId, userId))
                    .isInstanceOf(EntityNotFoundException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void assignGymManager_UserNotFound() {
            // [Given]
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(userRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> userService.assignGymManager(gymId, invalidId))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("unassignGymManager() 메서드 테스트")
    class UnassignGymManagerTest {

        @Test
        @DisplayName("암장 관리자 해제 성공")
        void unassignGymManager_Success() {
            // [Given]
            given(userRepository.findByUsername(managerUsername)).willReturn(Optional.of(mockManager));

            // [When]
            UserDetailDTO result = userService.unassignGymManager(managerUsername);

            // [Then]
            assertThat(result.getUsername()).isEqualTo(managerUsername);
            // 역할이 MEMBER로 변경되었는지 확인
            assertThat(mockManager.getRole()).isEqualTo(Role.MEMBER);
            assertThat(mockManager.getGym()).isNull();
        }

        @Test
        @DisplayName("실패: 존재하지 않는 username 요청시 예외 발생")
        void unassignGymManager_UserNotFound() {
            // [Given]
            given(userRepository.findByUsername("unknown")).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> userService.unassignGymManager("unknown"))
                    .isInstanceOf(EntityNotFoundException.class);
        }
    }
}
