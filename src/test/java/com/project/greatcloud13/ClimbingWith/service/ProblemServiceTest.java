package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ProblemCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymLevelNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.problem.ProblemNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.setting.SettingNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
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
public class ProblemServiceTest {

    @InjectMocks
    private ProblemService problemService;

    @Mock private ProblemRepository problemRepository;
    @Mock private GymLevelRepository gymLevelRepository;
    @Mock private GymRepository gymRepository;
    @Mock private WallSettingRepository settingRepository;
    @Mock private UserRepository userRepository;

//   ========================= Mock Objects =========================
    private Gym mockGym;
    private Gym otherGym;
    private GymLevel mockGymLevel;
    private Setting mockSetting;
    private Problem mockProblem;
    private User mockManager;
    private User mockMember;
    private User mockAdmin;
//    ===============================================================

    Long gymId = 100L;
    Long otherGymId = 101L;
    Long gymLevelId = 200L;
    Long settingId = 300L;
    Long problemId = 400L;
    Long userId = 500L;
    Long memberId = 501L;
    Long adminId = 502L;
    Long invalidId = 999L;

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").build();
        ReflectionTestUtils.setField(mockGym, "id", gymId);

        otherGym = Gym.builder().gymName("다른 암장").build();
        ReflectionTestUtils.setField(otherGym, "id", otherGymId);

        mockGymLevel = GymLevel.builder().gym(mockGym).levelName("빨강").build();
        ReflectionTestUtils.setField(mockGymLevel, "id", gymLevelId);

        mockSetting = Setting.builder().gym(mockGym).build();
        ReflectionTestUtils.setField(mockSetting, "id", settingId);

        mockProblem = Problem.builder()
                .title("테스트 문제")
                .gym(mockGym)
                .setting(mockSetting)
                .gymLevel(mockGymLevel)
                .problemType(ProblemType.BOULDER)
                .description("테스트 설명")
                .build();
        ReflectionTestUtils.setField(mockProblem, "id", problemId);

        // 암장 관리자 (mockGym 소속)
        mockManager = User.builder().username("manager").email("m@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockManager, "id", userId);
        ReflectionTestUtils.setField(mockManager, "role", Role.GYM_MANAGER);
        ReflectionTestUtils.setField(mockManager, "gym", mockGym);

        // 일반 회원 (암장 소속 없음)
        mockMember = User.builder().username("member").email("mem@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockMember, "id", memberId);
        ReflectionTestUtils.setField(mockMember, "role", Role.MEMBER);

        // 관리자
        mockAdmin = User.builder().username("admin").email("admin@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockAdmin, "id", adminId);
        ReflectionTestUtils.setField(mockAdmin, "role", Role.ADMIN);
    }

    @Nested
    @DisplayName("createProblem() 메서드 테스트")
    class CreateProblemTest {

        @Test
        @DisplayName("암장 관리자가 문제 생성 성공")
        void createProblem_Success() {
            // [Given]
            ProblemCreateDTO request = new ProblemCreateDTO();
            request.setSettingId(settingId);
            request.setGymLevelId(gymLevelId);
            request.setTitle("새 문제");
            request.setProblemType(ProblemType.BOULDER);
            request.setDescription("설명");

            given(userRepository.findById(userId)).willReturn(Optional.of(mockManager));
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting));
            given(gymLevelRepository.findById(gymLevelId)).willReturn(Optional.of(mockGymLevel));
            given(problemRepository.save(any(Problem.class))).willReturn(mockProblem);

            // [When]
            ProblemDTO result = problemService.createProblem(request, userId);

            // [Then]
            assertThat(result).isNotNull();
            assertThat(result.getTitle()).isEqualTo(mockProblem.getTitle());
            verify(problemRepository, times(1)).save(any(Problem.class));
        }

        @Test
        @DisplayName("실패: 다른 암장 관리자가 문제 생성 시도")
        void createProblem_AccessDenied() {
            // [Given]
            ProblemCreateDTO request = new ProblemCreateDTO();
            request.setSettingId(settingId);
            request.setGymLevelId(gymLevelId);

            // otherGym 소속 관리자가 mockGym의 세팅에 접근
            User otherManager = User.builder().username("other").email("o@test.com").password("pw").build();
            ReflectionTestUtils.setField(otherManager, "id", 600L);
            ReflectionTestUtils.setField(otherManager, "role", Role.GYM_MANAGER);
            ReflectionTestUtils.setField(otherManager, "gym", otherGym);

            given(userRepository.findById(600L)).willReturn(Optional.of(otherManager));
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting));

            // [When & Then]
            assertThatThrownBy(() -> problemService.createProblem(request, 600L))
                    .isInstanceOf(GymAccessDeniedException.class);

            verify(problemRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 세팅 ID 요청시 예외 발생")
        void createProblem_SettingNotFound() {
            // [Given]
            ProblemCreateDTO request = new ProblemCreateDTO();
            request.setSettingId(invalidId);
            request.setGymLevelId(gymLevelId);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockManager));
            given(settingRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemService.createProblem(request, userId))
                    .isInstanceOf(SettingNotFoundException.class);

            verify(problemRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 레벨 ID 요청시 예외 발생")
        void createProblem_GymLevelNotFound() {
            // [Given]
            ProblemCreateDTO request = new ProblemCreateDTO();
            request.setSettingId(settingId);
            request.setGymLevelId(invalidId);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockManager));
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting));
            given(gymLevelRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemService.createProblem(request, userId))
                    .isInstanceOf(GymLevelNotFoundException.class);

            verify(problemRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getProblem() 메서드 테스트")
    class GetProblemTest {

        @Test
        @DisplayName("문제 단건 조회 성공")
        void getProblem_Success() {
            // [Given]
            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem));

            // [When]
            ProblemDTO result = problemService.getProblem(problemId);

            // [Then]
            assertThat(result.getId()).isEqualTo(problemId);
            assertThat(result.getTitle()).isEqualTo(mockProblem.getTitle());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 문제 ID 요청시 예외 발생")
        void getProblem_NotFound() {
            // [Given]
            given(problemRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemService.getProblem(invalidId))
                    .isInstanceOf(ProblemNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getProblemPageByGym() 메서드 테스트")
    class GetProblemPageByGymTest {

        @Test
        @DisplayName("암장 기준 문제 페이지 조회 성공")
        void getProblemPageByGym_Success() {
            // [Given]
            Page<Problem> mockPage = new PageImpl<>(List.of(mockProblem), PageRequest.of(0, 10), 1);

            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(settingRepository.findAllByGymAndIsActive(mockGym, true)).willReturn(List.of(mockSetting));
            given(problemRepository.findAllBySettingIn(List.of(mockSetting), PageRequest.of(0, 10))).willReturn(mockPage);

            // [When]
            Page<ProblemDTO> result = problemService.getProblemPageByGym(gymId, 0);

            // [Then]
            assertThat(result.getTotalElements()).isEqualTo(1);
            assertThat(result.getContent().getFirst().getTitle()).isEqualTo(mockProblem.getTitle());
        }

        @Test
        @DisplayName("실패: 활성 세팅이 없을 때 예외 발생")
        void getProblemPageByGym_NoActiveSetting() {
            // [Given]
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(settingRepository.findAllByGymAndIsActive(mockGym, true)).willReturn(List.of());

            // [When & Then]
            assertThatThrownBy(() -> problemService.getProblemPageByGym(gymId, 0))
                    .isInstanceOf(SettingNotFoundException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 암장 ID 요청시 예외 발생")
        void getProblemPageByGym_GymNotFound() {
            // [Given]
            given(gymRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemService.getProblemPageByGym(invalidId, 0))
                    .isInstanceOf(GymNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateProblem() 메서드 테스트")
    class UpdateProblemTest {

        @Test
        @DisplayName("문제 수정 성공")
        void updateProblem_Success() {
            // [Given]
            ProblemUpdateDTO request = new ProblemUpdateDTO();
            request.setTitle("수정된 제목");
            request.setProblemType(ProblemType.BOULDER);
            request.setGymLevelId(gymLevelId);
            request.setDescription("수정된 설명");

            given(userRepository.findById(userId)).willReturn(Optional.of(mockManager));
            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem));
            given(gymLevelRepository.findById(gymLevelId)).willReturn(Optional.of(mockGymLevel));

            // [When]
            ProblemDTO result = problemService.updateProblem(problemId, request, userId);

            // [Then]
            assertThat(result.getTitle()).isEqualTo("수정된 제목");
        }

        @Test
        @DisplayName("실패: 다른 암장 관리자가 수정 시도")
        void updateProblem_AccessDenied() {
            // [Given]
            ProblemUpdateDTO request = new ProblemUpdateDTO();
            request.setGymLevelId(gymLevelId);

            User otherManager = User.builder().username("other").email("o@test.com").password("pw").build();
            ReflectionTestUtils.setField(otherManager, "id", 600L);
            ReflectionTestUtils.setField(otherManager, "role", Role.GYM_MANAGER);
            ReflectionTestUtils.setField(otherManager, "gym", otherGym);

            given(userRepository.findById(600L)).willReturn(Optional.of(otherManager));
            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem));

            // [When & Then]
            assertThatThrownBy(() -> problemService.updateProblem(problemId, request, 600L))
                    .isInstanceOf(GymAccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("deleteProblem() 메서드 테스트")
    class DeleteProblemTest {

        @Test
        @DisplayName("문제 삭제 성공")
        void deleteProblem_Success() {
            // [Given]
            given(userRepository.findById(userId)).willReturn(Optional.of(mockManager));
            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem));

            // [When]
            problemService.deleteProblem(problemId, userId);

            // [Then]
            verify(problemRepository, times(1)).deleteById(problemId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 문제 삭제 시도")
        void deleteProblem_NotFound() {
            // [Given]
            given(userRepository.findById(userId)).willReturn(Optional.of(mockManager));
            given(problemRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemService.deleteProblem(invalidId, userId))
                    .isInstanceOf(ProblemNotFoundException.class);

            verify(problemRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("실패: 다른 암장 관리자가 삭제 시도")
        void deleteProblem_AccessDenied() {
            // [Given]
            User otherManager = User.builder().username("other").email("o@test.com").password("pw").build();
            ReflectionTestUtils.setField(otherManager, "id", 600L);
            ReflectionTestUtils.setField(otherManager, "role", Role.GYM_MANAGER);
            ReflectionTestUtils.setField(otherManager, "gym", otherGym);

            given(userRepository.findById(600L)).willReturn(Optional.of(otherManager));
            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem));

            // [When & Then]
            assertThatThrownBy(() -> problemService.deleteProblem(problemId, 600L))
                    .isInstanceOf(GymAccessDeniedException.class);

            verify(problemRepository, never()).deleteById(any());
        }
    }
}
