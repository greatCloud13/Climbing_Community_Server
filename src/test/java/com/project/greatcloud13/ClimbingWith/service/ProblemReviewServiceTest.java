package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.exception.problem.ProblemNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.review.ProblemReviewNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.review.ReviewAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemReviewRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class ProblemReviewServiceTest {

    @InjectMocks
    private ProblemReviewService problemReviewService;

    @Mock private ProblemReviewRepository problemReviewRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private UserRepository userRepository;

//   ========================= Mock Objects =========================
    private Gym mockGym;
    private GymLevel mockGymLevel;
    private Setting mockSetting;
    private Problem mockProblem;
    private User mockUser1;
    private User mockUser2;
    private ProblemReview mockReview;
//    ===============================================================

    Long gymId = 100L;
    Long problemId = 400L;
    Long userId = 500L;
    Long userId2 = 501L;
    Long reviewId = 600L;
    Long invalidId = 999L;

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").build();
        ReflectionTestUtils.setField(mockGym, "id", gymId);

        mockGymLevel = GymLevel.builder().gym(mockGym).levelName("빨강").build();

        mockSetting = Setting.builder().gym(mockGym).build();

        mockProblem = Problem.builder()
                .title("테스트 문제")
                .gym(mockGym)
                .setting(mockSetting)
                .gymLevel(mockGymLevel)
                .build();
        ReflectionTestUtils.setField(mockProblem, "id", problemId);

        mockUser1 = User.builder().username("tester1").email("t1@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockUser1, "id", userId);

        mockUser2 = User.builder().username("tester2").email("t2@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockUser2, "id", userId2);

        mockReview = ProblemReview.builder()
                .problem(mockProblem)
                .user(mockUser1)
                .problemHint("힌트입니다")
                .evaluation(4)
                .build();
        ReflectionTestUtils.setField(mockReview, "id", reviewId);
    }

    @Nested
    @DisplayName("createReview() 메서드 테스트")
    class CreateReviewTest {

        @Test
        @DisplayName("리뷰 작성 성공 - 평점 재계산 검증")
        void createReview_Success() {
            // [Given]
            ProblemReviewCreateDTO request = new ProblemReviewCreateDTO();
            request.setProblemId(problemId);
            request.setProblemHint("힌트");
            request.setEvaluation(4);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem));
            given(problemReviewRepository.save(any(ProblemReview.class))).willReturn(mockReview);
            given(problemReviewRepository.countByProblemId(problemId)).willReturn(1L);
            given(problemReviewRepository.sumEvaluationByProblemId(problemId)).willReturn(4L);

            // [When]
            ProblemReviewDTO result = problemReviewService.createReview(userId, request);

            // [Then]
            assertThat(result).isNotNull();
            assertThat(result.getEvaluation()).isEqualTo(4);
            // 평점 재계산: 4/1 = 4.0
            assertThat(mockProblem.getEvaluation()).isEqualTo(4.0f);
            verify(problemReviewRepository, times(1)).save(any(ProblemReview.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void createReview_UserNotFound() {
            // [Given]
            ProblemReviewCreateDTO request = new ProblemReviewCreateDTO();
            request.setProblemId(problemId);
            given(userRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemReviewService.createReview(invalidId, request))
                    .isInstanceOf(UserNotFoundException.class);

            verify(problemReviewRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 문제 ID 요청시 예외 발생")
        void createReview_ProblemNotFound() {
            // [Given]
            ProblemReviewCreateDTO request = new ProblemReviewCreateDTO();
            request.setProblemId(invalidId);
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(problemRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemReviewService.createReview(userId, request))
                    .isInstanceOf(ProblemNotFoundException.class);

            verify(problemReviewRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("updateReview() 메서드 테스트")
    class UpdateReviewTest {

        @Test
        @DisplayName("리뷰 수정 성공 - 평점 재계산 검증")
        void updateReview_Success() {
            // [Given]
            ProblemReviewUpdateDTO request = new ProblemReviewUpdateDTO();
            request.setProblemHint("수정된 힌트");
            request.setEvaluation(5);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(problemReviewRepository.findById(reviewId)).willReturn(Optional.of(mockReview));
            given(problemReviewRepository.countByProblemId(problemId)).willReturn(1L);
            given(problemReviewRepository.sumEvaluationByProblemId(problemId)).willReturn(5L);

            // [When]
            ProblemReviewDTO result = problemReviewService.updateReview(reviewId, userId, request);

            // [Then]
            assertThat(result.getProblemHint()).isEqualTo("수정된 힌트");
            assertThat(result.getEvaluation()).isEqualTo(5);
            // 평점 재계산: 5/1 = 5.0
            assertThat(mockProblem.getEvaluation()).isEqualTo(5.0f);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 리뷰 수정 시도시 예외 발생")
        void updateReview_AccessDenied() {
            // [Given]
            ProblemReviewUpdateDTO request = new ProblemReviewUpdateDTO();
            request.setEvaluation(3);

            given(userRepository.findById(userId2)).willReturn(Optional.of(mockUser2));
            given(problemReviewRepository.findById(reviewId)).willReturn(Optional.of(mockReview));

            // [When & Then] mockReview는 mockUser1 소유
            assertThatThrownBy(() -> problemReviewService.updateReview(reviewId, userId2, request))
                    .isInstanceOf(ReviewAccessDeniedException.class);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 리뷰 ID 요청시 예외 발생")
        void updateReview_ReviewNotFound() {
            // [Given]
            ProblemReviewUpdateDTO request = new ProblemReviewUpdateDTO();

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(problemReviewRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemReviewService.updateReview(invalidId, userId, request))
                    .isInstanceOf(ProblemReviewNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteReview() 메서드 테스트")
    class DeleteReviewTest {

        @Test
        @DisplayName("리뷰 삭제 성공")
        void deleteReview_Success() {
            // [Given]
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(problemReviewRepository.findById(reviewId)).willReturn(Optional.of(mockReview));

            // [When]
            problemReviewService.deleteReview(reviewId, userId);

            // [Then]
            verify(problemReviewRepository, times(1)).deleteById(reviewId);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 리뷰 삭제 시도시 예외 발생")
        void deleteReview_AccessDenied() {
            // [Given]
            given(userRepository.findById(userId2)).willReturn(Optional.of(mockUser2));
            given(problemReviewRepository.findById(reviewId)).willReturn(Optional.of(mockReview));

            // [When & Then]
            assertThatThrownBy(() -> problemReviewService.deleteReview(reviewId, userId2))
                    .isInstanceOf(ReviewAccessDeniedException.class);

            verify(problemReviewRepository, never()).deleteById(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 리뷰 삭제 시도")
        void deleteReview_ReviewNotFound() {
            // [Given]
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(problemReviewRepository.findById(invalidId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> problemReviewService.deleteReview(invalidId, userId))
                    .isInstanceOf(ProblemReviewNotFoundException.class);

            verify(problemReviewRepository, never()).deleteById(any());
        }
    }
}
