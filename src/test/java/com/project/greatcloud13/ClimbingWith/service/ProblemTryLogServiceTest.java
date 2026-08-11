package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ProblemTryLogDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.exception.problem.ProblemNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemTryLogRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

/*==================테스트 코드 작성 규칙===========================
1. 테스트 대상 Class 내부 메소드 외의 의존성은 Mock 주입을 통해 동작 정의
2. 테스트에 사용되는 중복되는 파라미터 및 Entity는 클래스 영역에 정의
3. 테스트 코드 작성 양식은 [Given], [When], [Then] 양식으로 작성
4. verify를 통한 메소드의 동작 여부 또한 검증
 */

@ExtendWith(MockitoExtension.class)
public class ProblemTryLogServiceTest {

    @InjectMocks
    private ProblemTryLogService problemTryLogService;

    @Mock private ProblemTryLogRepository problemTryLogRepository;
    @Mock private UserRepository userRepository;
    @Mock private ProblemRepository problemRepository;

    private Gym mockGym;
    private GymLevel mockGymLevel;
    private Setting mockSetting;
    private Problem mockProblem;
    private User mockUser;

    Long problemId = 400L;
    Long userId = 500L;
    Long invalidId = 999L;

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").build();
        mockGymLevel = GymLevel.builder().gym(mockGym).levelName("빨강").build();
        mockSetting = Setting.builder().gym(mockGym).build();

        mockProblem = Problem.builder()
                .title("테스트 문제").gym(mockGym).setting(mockSetting).gymLevel(mockGymLevel).build();
        ReflectionTestUtils.setField(mockProblem, "id", problemId);

        mockUser = User.builder().username("tester").email("t@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockUser, "id", userId);
    }

    @Nested
    @DisplayName("getProblemTryLogsByUserAndProblem() 메서드 테스트")
    class GetProblemTryLogsByUserAndProblemTest {

        @Test
        @DisplayName("문제별 내 시도 기록 조회 성공")
        void getProblemTryLogsByUserAndProblem_Success() {
            // [Given]
            int page = 0, size = 5;
            Pageable pageable = PageRequest.of(page, size, Sort.by("tryDate").descending());

            Page<ProblemTryLog> mockPage = TestFixture.createMockPage(
                    () -> ProblemTryLog.builder().user(mockUser).problem(mockProblem).dropPoint(4).build(), size);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem));
            given(problemTryLogRepository.findAllByUserAndProblem(mockUser, mockProblem, pageable)).willReturn(mockPage);

            // [When]
            Page<ProblemTryLogDTO> result = problemTryLogService.getProblemTryLogsByUserAndProblem(userId, problemId, page, size);

            // [Then]
            assertThat(result.getSize()).isEqualTo(size);
            assertThat(result.getContent().getFirst().getProblemId()).isEqualTo(problemId);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void getProblemTryLogsByUserAndProblem_UserNotFound() {
            // [Given]
            given(userRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemTryLogService.getProblemTryLogsByUserAndProblem(invalidId, problemId, 0, 10))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 문제 ID 요청시 예외 발생")
        void getProblemTryLogsByUserAndProblem_ProblemNotFound() {
            // [Given]
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser));
            given(problemRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemTryLogService.getProblemTryLogsByUserAndProblem(userId, invalidId, 0, 10))
                    .isInstanceOf(ProblemNotFoundException.class);
        }
    }
}
