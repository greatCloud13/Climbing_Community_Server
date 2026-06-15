package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ClearRecordCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordResponseDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordSummaryDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.exception.clearrecord.ClearRecordAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.clearrecord.ClearRecordNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.problem.ProblemNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.sector.SectorNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.setting.SettingNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.*;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
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
public class ClearRecordServiceTest {

    @InjectMocks
    private ClearRecordService clearRecordService;

    @Mock private UserRepository userRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private ClearRecordRepository clearRecordRepository;
    @Mock private GymRepository gymRepository;
    @Mock private WallSettingRepository settingRepository;
    @Mock private SectorRepository sectorRepository;
    @Mock private ClearRecordRepositoryCustom clearRecordRepositoryCustom;

//   ========================= Mock Objects =========================
    private Gym mockGym1;
    private Gym mockGym2;
    private Sector mockSector1;
    private Setting mockSetting1;
    private Setting mockSetting2;
    private Problem mockProblem1;
    private Problem mockProblem2;
    private User mockUser1;
    private User mockUser2;
    private User mockAdmin;
    private GymLevel mockGymLevel1;
    private ClearRecord mockClearRecord1;
//    ===============================================================

//    ==================== Mock Object parameter ====================
    Long gymId = 100L;
    Long gymId2 = 101L;
    Long sectorId = 200L;
    Long settingId = 300L;
    Long settingId2 = 301L;
    Long problemId = 400L;
    Long problemId2 = 401L;
    Long userId = 500L;
    Long userId2 = 501L;
    Long adminUserId = 5001L;
    Long clearRecordId = 600L;

    Long invalidUserId = 999L;
    Long invalidProblemId = 998L;
    Long invalidGymId = 997L;
    Long invalidSettingId = 996L;
    Long invalidSectorId = 995L;
    Long invalidClearRecordId = 994L;

    LocalDate past = LocalDate.of(2026, 3, 10);
    String videoUrl = "https://s3.aws.com/video/climbing.mp4";
//    ===============================================================

    @BeforeEach
    void setUp() {
        mockGym1 = Gym.builder().gymName("테스트 암장1").build();
        ReflectionTestUtils.setField(mockGym1, "id", gymId);

        mockGym2 = Gym.builder().gymName("테스트 암장2").build();
        ReflectionTestUtils.setField(mockGym2, "id", gymId2);

        mockSector1 = Sector.builder().gym(mockGym1).sectorName("테스트 섹터1").build();
        ReflectionTestUtils.setField(mockSector1, "id", sectorId);

        mockSetting1 = Setting.builder().gym(mockGym1).sector(mockSector1).build();
        ReflectionTestUtils.setField(mockSetting1, "id", settingId);

        mockSetting2 = Setting.builder().gym(mockGym2).build();
        ReflectionTestUtils.setField(mockSetting2, "id", settingId2);

        mockGymLevel1 = GymLevel.builder().gym(mockGym1).levelName("테스트 난이도 1").build();

        mockProblem1 = Problem.builder().title("테스트 문제1").gym(mockGym1).setting(mockSetting1).gymLevel(mockGymLevel1).build();
        ReflectionTestUtils.setField(mockProblem1, "id", problemId);

        mockProblem2 = Problem.builder().title("테스트 문제2").gym(mockGym2).setting(mockSetting2).gymLevel(mockGymLevel1).build();
        ReflectionTestUtils.setField(mockProblem2, "id", problemId2);

        mockUser1 = User.builder().username("tester1").email("t1@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockUser1, "id", userId);

        mockUser2 = User.builder().username("tester2").email("t2@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockUser2, "id", userId2);

        mockAdmin = User.builder().username("admin").email("admin@test.com").password("pw").build();
        ReflectionTestUtils.setField(mockAdmin, "id", adminUserId);
        ReflectionTestUtils.setField(mockAdmin, "role", Role.ADMIN);

        mockClearRecord1 = ClearRecord.builder()
                .user(mockUser1)
                .gym(mockGym1)
                .setting(mockSetting1)
                .problem(mockProblem1)
                .videoUrl(videoUrl)
                .clearDate(past)
                .build();
        ReflectionTestUtils.setField(mockClearRecord1, "id", clearRecordId);
    }

    @Nested
    @DisplayName("createClearRecord() 메서드 테스트")
    class CreateClearRecordTest {

        @Test
        @DisplayName("완등 기록 작성 성공")
        void createClearRecord_Success() {
            // [Given]
            ClearRecordCreateDTO request = new ClearRecordCreateDTO(problemId, videoUrl, LocalDate.now());

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem1));

            // [When]
            ClearRecordResponseDTO response = clearRecordService.createClearRecord(userId, request);

            // [Then]
            assertThat(response).isNotNull();
            assertThat(response.getUsername()).isEqualTo(mockUser1.getUsername());
            assertThat(response.getVideoUrl()).isEqualTo(videoUrl);
            assertThat(mockProblem1.getClearUserCount()).isEqualTo(1);

            verify(clearRecordRepository, times(1)).save(any(ClearRecord.class));
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void createClearRecord_UserNotFound() {
            // [Given]
            ClearRecordCreateDTO request = new ClearRecordCreateDTO(problemId, videoUrl, LocalDate.now());
            given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.createClearRecord(invalidUserId, request))
                    .isInstanceOf(UserNotFoundException.class);

            verify(clearRecordRepository, never()).save(any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 문제 ID 요청시 예외 발생")
        void createClearRecord_ProblemNotFound() {
            // [Given]
            ClearRecordCreateDTO request = new ClearRecordCreateDTO(invalidProblemId, videoUrl, LocalDate.now());
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(problemRepository.findById(invalidProblemId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.createClearRecord(userId, request))
                    .isInstanceOf(ProblemNotFoundException.class);

            verify(clearRecordRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getClearRecordSummaryByUserId() 메서드 테스트")
    class GetClearRecordSummaryByUserIdTest {

        @Test
        @DisplayName("완등 기록 조회 성공")
        void getClearRecordSummaryByUserId_Success() {
            // [Given]
            int page = 0, size = 5;
            Pageable pageable = PageRequest.of(page, size);

            Page<ClearRecord> mockPage = TestFixture.createMockPage(() -> ClearRecord.builder()
                    .user(mockUser1).gym(mockGym1).setting(mockSetting1)
                    .problem(mockProblem1).videoUrl(videoUrl).clearDate(LocalDate.now()).build(), size);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(clearRecordRepository.findAllByUserOrderByClearDateDesc(mockUser1, pageable)).willReturn(mockPage);

            // [When]
            Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByUserId(userId, page, size);

            // [Then]
            assertThat(result.getSize()).isEqualTo(size);
            assertThat(result.getContent().getFirst().getUsername()).isEqualTo(mockUser1.getUsername());
            verify(clearRecordRepository, times(1)).findAllByUserOrderByClearDateDesc(mockUser1, pageable);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void getClearRecordSummaryByUserId_UserNotFound() {
            // [Given]
            given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.getClearRecordSummaryByUserId(invalidUserId, 0, 10))
                    .isInstanceOf(UserNotFoundException.class);

            verify(clearRecordRepository, never()).findAllByUserOrderByClearDateDesc(any(), any());
        }
    }

    @Nested
    @DisplayName("getClearRecordSummaryByUserIdAndGym() 메서드 테스트")
    class GetClearRecordSummaryByUserIdAndGymTest {

        @Test
        @DisplayName("완등 기록 조회 성공")
        void getClearRecordSummaryByUserIdAndGym_Success() {
            // [Given]
            int page = 0, size = 5;
            Pageable pageable = PageRequest.of(page, size);

            Page<ClearRecord> mockPage = TestFixture.createMockPage(() -> ClearRecord.builder()
                    .user(mockUser1).gym(mockGym1).setting(mockSetting1)
                    .problem(mockProblem1).videoUrl(videoUrl).clearDate(LocalDate.now()).build(), size);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym1));
            given(clearRecordRepository.findAllByUserAndGymOrderByClearDateDesc(mockUser1, mockGym1, pageable)).willReturn(mockPage);

            // [When]
            Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByUserIdAndGym(userId, gymId, page, size);

            // [Then]
            assertThat(result.getSize()).isEqualTo(size);
            assertThat(result.getContent().getFirst().getUsername()).isEqualTo(mockUser1.getUsername());
            verify(clearRecordRepository, times(1)).findAllByUserAndGymOrderByClearDateDesc(mockUser1, mockGym1, pageable);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void getClearRecordSummaryByUserIdAndGym_UserNotFound() {
            // [Given]
            given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.getClearRecordSummaryByUserIdAndGym(invalidUserId, gymId, 0, 10))
                    .isInstanceOf(UserNotFoundException.class);

            verify(clearRecordRepository, never()).findAllByUserAndGymOrderByClearDateDesc(any(), any(), any());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 암장 ID 요청시 예외 발생")
        void getClearRecordSummaryByUserIdAndGym_GymNotFound() {
            // [Given]
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(gymRepository.findById(invalidGymId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.getClearRecordSummaryByUserIdAndGym(userId, invalidGymId, 0, 10))
                    .isInstanceOf(GymNotFoundException.class);

            verify(clearRecordRepository, never()).findAllByUserAndGymOrderByClearDateDesc(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("getClearRecordSummaryByUserIdAndSettingId() 메서드 테스트")
    class GetClearRecordSummaryByUserIdAndSettingIdTest {

        @Test
        @DisplayName("완등 기록 조회 성공")
        void getClearRecordSummaryByUserIdAndSettingId_Success() {
            // [Given]
            int page = 0, size = 5;
            Pageable pageable = PageRequest.of(page, size);

            Page<ClearRecord> mockPage = TestFixture.createMockPage(() -> ClearRecord.builder()
                    .user(mockUser1).gym(mockGym1).setting(mockSetting1)
                    .problem(mockProblem1).videoUrl(videoUrl).clearDate(LocalDate.now()).build(), size);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting1));
            given(clearRecordRepository.findAllByUserAndSettingOrderByClearDateDesc(mockUser1, mockSetting1, pageable)).willReturn(mockPage);

            // [When]
            Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByUserIdAndSettingId(userId, settingId, page, size);

            // [Then]
            assertThat(result.getSize()).isEqualTo(size);
            verify(clearRecordRepository, times(1)).findAllByUserAndSettingOrderByClearDateDesc(mockUser1, mockSetting1, pageable);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 세팅 ID 요청시 예외 발생")
        void getClearRecordSummaryByUserIdAndSettingId_SettingNotFound() {
            // [Given]
            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(settingRepository.findById(invalidSettingId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.getClearRecordSummaryByUserIdAndSettingId(userId, invalidSettingId, 0, 10))
                    .isInstanceOf(SettingNotFoundException.class);

            verify(clearRecordRepository, never()).findAllByUserAndSettingOrderByClearDateDesc(any(), any(), any());
        }
    }

    @Nested
    @DisplayName("getClearRecordSummaryByProblemExistVideoUrl() 메서드 테스트")
    class GetClearRecordSummaryByProblemExistVideoUrlTest {

        @Test
        @DisplayName("영상 있는 완등 기록 조회 성공")
        void getClearRecordSummaryByProblemExistVideoUrl_Success() {
            // [Given]
            int page = 0, size = 5;
            Pageable pageable = PageRequest.of(page, size);

            Page<ClearRecord> mockPage = TestFixture.createMockPage(() -> ClearRecord.builder()
                    .user(mockUser1).gym(mockGym1).setting(mockSetting1)
                    .problem(mockProblem1).videoUrl(videoUrl).clearDate(LocalDate.now()).build(), size);

            given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem1));
            given(clearRecordRepository.findAllByProblemAndVideoUrlIsNotNull(mockProblem1, pageable)).willReturn(mockPage);

            // [When]
            Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByProblemExistVideoUrl(problemId, page, size);

            // [Then]
            assertThat(result.getSize()).isEqualTo(size);
            verify(clearRecordRepository, times(1)).findAllByProblemAndVideoUrlIsNotNull(mockProblem1, pageable);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 문제 ID 요청시 예외 발생")
        void getClearRecordSummaryByProblemExistVideoUrl_ProblemNotFound() {
            // [Given]
            given(problemRepository.findById(invalidProblemId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.getClearRecordSummaryByProblemExistVideoUrl(invalidProblemId, 0, 10))
                    .isInstanceOf(ProblemNotFoundException.class);

            verify(clearRecordRepository, never()).findAllByProblemAndVideoUrlIsNotNull(any(), any());
        }
    }

    @Nested
    @DisplayName("getClearRecordSummaryBySectorExistVideoUrl() 메서드 테스트")
    class GetClearRecordSummaryBySectorExistVideoUrlTest {

        @Test
        @DisplayName("섹터 영상 있는 완등 기록 조회 성공")
        void getClearRecordSummaryBySectorExistVideoUrl_Success() {
            // [Given]
            int page = 0, size = 10;
            Pageable pageable = PageRequest.of(page, size);

            Page<ClearRecord> mockPage = TestFixture.createMockPage(() -> ClearRecord.builder()
                    .user(mockUser1).gym(mockGym1).setting(mockSetting1)
                    .problem(mockProblem1).videoUrl(videoUrl).clearDate(LocalDate.now()).build(), size);

            given(sectorRepository.findById(sectorId)).willReturn(Optional.of(mockSector1));
            given(settingRepository.findTopBySectorAndIsActiveOrderBySettingDateDesc(mockSector1, true)).willReturn(Optional.of(mockSetting1));
            given(clearRecordRepository.findAllBySettingAndVideoUrlIsNotNull(mockSetting1, pageable)).willReturn(mockPage);

            // [When]
            Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryBySectorExistVideoUrl(sectorId, page, size);

            // [Then]
            assertThat(result.getSize()).isEqualTo(size);
            verify(clearRecordRepository, times(1)).findAllBySettingAndVideoUrlIsNotNull(mockSetting1, pageable);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 섹터 ID 요청시 예외 발생")
        void getClearRecordSummaryBySectorExistVideoUrl_SectorNotFound() {
            // [Given]
            given(sectorRepository.findById(invalidSectorId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.getClearRecordSummaryBySectorExistVideoUrl(invalidSectorId, 0, 10))
                    .isInstanceOf(SectorNotFoundException.class);

            verify(clearRecordRepository, never()).findAllBySettingAndVideoUrlIsNotNull(any(), any());
        }

        @Test
        @DisplayName("실패: 활성 세팅이 없을 때 예외 발생")
        void getClearRecordSummaryBySectorExistVideoUrl_SettingNotFound() {
            // [Given]
            given(sectorRepository.findById(sectorId)).willReturn(Optional.of(mockSector1));
            given(settingRepository.findTopBySectorAndIsActiveOrderBySettingDateDesc(mockSector1, true)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.getClearRecordSummaryBySectorExistVideoUrl(sectorId, 0, 10))
                    .isInstanceOf(SettingNotFoundException.class);

            verify(clearRecordRepository, never()).findAllBySettingAndVideoUrlIsNotNull(any(), any());
        }
    }

    @Nested
    @DisplayName("updateClearRecord() 메서드 테스트")
    class UpdateClearRecordTest {

        @Test
        @DisplayName("완등 기록 업데이트 성공 - 문제와 영상 모두 변경")
        void updateClearRecord_Success() {
            // [Given]
            Long newProblemId = 50L;
            String newVideoUrl = "https://new-url.com/video.mp4";

            Problem newProblem = Problem.builder().title("새 문제").gym(mockGym1).setting(mockSetting1).gymLevel(mockGymLevel1).build();
            ReflectionTestUtils.setField(newProblem, "id", newProblemId);

            ClearRecordUpdateDTO request = new ClearRecordUpdateDTO();
            request.setProblemId(newProblemId);
            request.setVideoUrl(newVideoUrl);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(clearRecordRepository.findById(clearRecordId)).willReturn(Optional.of(mockClearRecord1));
            given(problemRepository.findById(newProblemId)).willReturn(Optional.of(newProblem));

            // [When]
            ClearRecordResponseDTO result = clearRecordService.updateClearRecord(userId, clearRecordId, request);

            // [Then]
            assertThat(result.getProblemId()).isEqualTo(newProblemId);
            assertThat(result.getVideoUrl()).isEqualTo(newVideoUrl);
        }

        @Test
        @DisplayName("실패: 존재하지 않는 완등 기록 요청시 예외 발생")
        void updateClearRecord_RecordNotFound() {
            // [Given]
            ClearRecordUpdateDTO request = new ClearRecordUpdateDTO();
            request.setProblemId(problemId);
            request.setVideoUrl(videoUrl);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(clearRecordRepository.findById(invalidClearRecordId)).willReturn(Optional.empty());

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.updateClearRecord(userId, invalidClearRecordId, request))
                    .isInstanceOf(ClearRecordNotFoundException.class);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 완등 기록 수정 시도시 예외 발생")
        void updateClearRecord_AccessDenied() {
            // [Given]
            ClearRecordUpdateDTO request = new ClearRecordUpdateDTO();
            request.setProblemId(problemId);
            request.setVideoUrl(videoUrl);

            given(userRepository.findById(userId2)).willReturn(Optional.of(mockUser2));
            given(clearRecordRepository.findById(clearRecordId)).willReturn(Optional.of(mockClearRecord1));

            // [When & Then] mockClearRecord1은 mockUser1 소유
            assertThatThrownBy(() -> clearRecordService.updateClearRecord(userId2, clearRecordId, request))
                    .isInstanceOf(ClearRecordAccessDeniedException.class);
        }

        @Test
        @DisplayName("ADMIN은 타인 완등 기록도 수정 가능")
        void updateClearRecord_AdminSuccess() {
            // [Given]
            ClearRecordUpdateDTO request = new ClearRecordUpdateDTO();
            request.setProblemId(problemId);
            request.setVideoUrl(videoUrl);

            given(userRepository.findById(adminUserId)).willReturn(Optional.of(mockAdmin));
            given(clearRecordRepository.findById(clearRecordId)).willReturn(Optional.of(mockClearRecord1));

            // [When] 예외 없이 성공해야 함
            ClearRecordResponseDTO result = clearRecordService.updateClearRecord(adminUserId, clearRecordId, request);

            // [Then]
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("deleteClearRecord() 메서드 테스트")
    class DeleteClearRecordTest {

        @Test
        @DisplayName("완등 기록 삭제 성공 - clearUserCount 감소 검증")
        void deleteClearRecord_Success() {
            // [Given]
            mockProblem1.addClearUserCount();
            assertThat(mockProblem1.getClearUserCount()).isEqualTo(1);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(clearRecordRepository.findById(clearRecordId)).willReturn(Optional.of(mockClearRecord1));

            // [When]
            clearRecordService.deleteClearRecord(userId, clearRecordId);

            // [Then]
            assertThat(mockProblem1.getClearUserCount()).isEqualTo(0);
            verify(clearRecordRepository, times(1)).delete(mockClearRecord1);
        }

        @Test
        @DisplayName("실패: 다른 사용자의 완등 기록 삭제 시도시 예외 발생")
        void deleteClearRecord_AccessDenied() {
            // [Given]
            given(userRepository.findById(userId2)).willReturn(Optional.of(mockUser2));
            given(clearRecordRepository.findById(clearRecordId)).willReturn(Optional.of(mockClearRecord1));

            // [When & Then]
            assertThatThrownBy(() -> clearRecordService.deleteClearRecord(userId2, clearRecordId))
                    .isInstanceOf(ClearRecordAccessDeniedException.class);

            verify(clearRecordRepository, never()).delete(any());
        }

        @Test
        @DisplayName("ADMIN은 타인 완등 기록도 삭제 가능")
        void deleteClearRecord_AdminSuccess() {
            // [Given]
            given(userRepository.findById(adminUserId)).willReturn(Optional.of(mockAdmin));
            given(clearRecordRepository.findById(clearRecordId)).willReturn(Optional.of(mockClearRecord1));

            // [When & Then] 예외 없이 성공
            clearRecordService.deleteClearRecord(adminUserId, clearRecordId);

            verify(clearRecordRepository, times(1)).delete(mockClearRecord1);
        }
    }
}
