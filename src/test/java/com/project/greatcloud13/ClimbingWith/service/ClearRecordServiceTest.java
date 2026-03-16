package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ClearRecordCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordResponseDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordSummaryDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.repository.ClearRecordRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.util.TestFixture;
import jakarta.persistence.EntityNotFoundException;
import org.aspectj.util.Reflection;
import org.hibernate.annotations.SQLJoinTableRestriction;
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

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ClearRecordServiceTest {

    @InjectMocks
    private ClearRecordService clearRecordService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private ProblemRepository problemRepository;
    @Mock
    private ClearRecordRepository clearRecordRepository;

//   ========================= Mock Objects =========================
    private Gym mockGym1;
    private Sector mockSector1;
    private Setting mockSetting1;
    private Problem mockProblem1;
    private User mockUser1;
    private User invalidUser1;
    private GymLevel mockGymLevel1;
//    ===============================================================

//    ==================== Mock Object parameter ====================
    Long gymId = 100L;
    String gymName = "테스트 암장1";

    Long sectorId = 200L;
    String sectorName = "테스트 섹터1";

    Long settingId = 300L;

    Long problemId = 400L;
    String problemName = "테스트 문제1";

    Long userId = 500L;
    String UserName = "테스트 사용자1";

    Long invalidUserId = 999L;

    LocalDate date = LocalDate.now();
    String videoUrl = "https://s3.aws.com/video/climbing.mp4";
    Long invalidProblemId = 999L;

//    ===============================================================

    @BeforeEach
    void setUp(){
//      Gym Mock Entity
        mockGym1 = Gym.builder().gymName(gymName).build();
        ReflectionTestUtils.setField(mockGym1, "id", gymId);

//      Sector Mock Entity
        mockSector1 = Sector.builder().gym(mockGym1).sectorName(sectorName).build();
        ReflectionTestUtils.setField(mockSector1, "id", sectorId);

//      Setting Mock Entity
        mockSetting1 = Setting.builder().gym(mockGym1).sector(mockSector1).build();
        ReflectionTestUtils.setField(mockSetting1, "id", settingId);

//      GymLevel Mock Entity
        mockGymLevel1 = GymLevel.builder().gym(mockGym1).levelName("테스트 난이도 1").build();

//      Problem Mock Entity
        mockProblem1 = Problem.builder().title(problemName).gym(mockGym1).setting(mockSetting1).gymLevel(mockGymLevel1).build();
        ReflectionTestUtils.setField(mockProblem1, "id", problemId);

//      User Mock Entity
        mockUser1 = User.builder().username("tester1").build();
        ReflectionTestUtils.setField(mockUser1, "id", userId);

//      invalidUser
        invalidUser1 = User.builder().username("invalidUser").build();
        ReflectionTestUtils.setField(invalidUser1, "id", invalidUserId);

    }

    @Nested
    @DisplayName("createClearRecord() 메서드 테스트")
    class CreateClearRecordTest {
        @Test
        @DisplayName("완등 기록 작성 성공")
        void createClearRecord_Success () {
        // [Given] 테스트 환경 셋업
        ClearRecordCreateDTO requestDTO = new ClearRecordCreateDTO(problemId, videoUrl, date);

//      Repository의 동작을 Mocking
        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
        given(problemRepository.findById(problemId)).willReturn(Optional.of(mockProblem1));

//      [When] 실제 로직 실행
        ClearRecordResponseDTO response = clearRecordService.createClearRecord(userId, requestDTO);

//      [Then] 결과 검증
//      1. 반환된 DTO 값이 의도한 대로 들어갔는지 확인
        assertThat(response).isNotNull();
        assertThat(response.getUsername()).isEqualTo(mockUser1.getUsername());
        assertThat(response.getClearDate()).isEqualTo(date.toString());
        assertThat(response.getVideoUrl()).isEqualTo(videoUrl);

//      2. 문제의 완등자 수가 증가했는지 확인 (problem.addClearUserCount() 호출 검증)
        assertThat(mockProblem1.getClearUserCount()).isEqualTo(1);

        verify(clearRecordRepository, times(1)).save(any(ClearRecord.class));
    }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void createClearRecord_UserNotFound () {
//      [Given]
        LocalDate createDate = LocalDate.now();
        ClearRecordCreateDTO requestDTO = new ClearRecordCreateDTO(invalidProblemId, videoUrl, createDate);

//      사용자를 찾을 수 없는 상황
        given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

//      [When & Then] 예외가 발생하는지 검증

        assertThatThrownBy(() -> clearRecordService.createClearRecord(invalidUserId, requestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("사용자를 찾을 수 없습니다.");

//      메소드가 실행되지 않았음을 검증
        verify(clearRecordRepository, times(0)).save(any());
    }

        @Test
        @DisplayName("실패: 존재하지 않는 문제 ID 요청시 예외 발생")
        void createClearRecord_ProblemIdNotFound () {
//      [Given]
        LocalDate createDate = LocalDate.now();
        ClearRecordCreateDTO requestDTO = new ClearRecordCreateDTO(invalidProblemId, videoUrl, createDate);

        given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));

//      [When & Then] 문제를 찾을 수 없는(문제가 존재하지 않는) 상황
        assertThatThrownBy(() -> clearRecordService.createClearRecord(userId, requestDTO))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("문제를 찾을 수 없습니다.");

//      메소드가 실행되지 않았음을 검증
        verify(clearRecordRepository, times(0)).save(any());
    }
    }

    @Nested
    @DisplayName("getClearRecordSummaryByUserId() 메서드 테스트")
    class GetClearRecordSummaryByUserIdTest{

        @Test
        @DisplayName("완등 기록 조회 성공")
        void getClearRecordSummaryByUserId_Success(){
//          [Given]
            int page = 0;
            int size = 5;

            Pageable pageable = PageRequest.of(page, size);

            Page<ClearRecord> mockClearRecord = TestFixture.createMockPage(()->ClearRecord.builder()
                    .user(mockUser1)
                    .gym(mockGym1)
                    .setting(mockSetting1)
                    .problem(mockProblem1)
                    .videoUrl(videoUrl)
                    .clearDate(LocalDate.now())
                    .build(), 5);

            given(userRepository.findById(userId)).willReturn(Optional.of(mockUser1));
            given(clearRecordRepository.findAllByUserOrderByClearDateDesc(mockUser1, pageable)).willReturn(mockClearRecord);

//          [When]
            Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByUserId(userId, page, size);

//          [Then]
//          요청한 size와 page 크기, 요청한 사용자의 정보가 일치하는지 검증,
            assertThat(result.getSize()).isEqualTo(size);
            assertThat(result.getPageable().getPageNumber()).isEqualTo(page);
            assertThat(result.getContent().getFirst().getUsername()).isEqualTo(mockUser1.getUsername());

            verify(clearRecordRepository, times(1)).findAllByUserOrderByClearDateDesc(mockUser1, pageable);

        }

        @Test
        @DisplayName("실패: 존재하지 않는 사용자 ID 요청시 예외 발생")
        void getClearRecordSummaryByUserId_Success_UserNotFound(){
//          [Given]
            int page = 0;
            int size = 10;

//          사용자를 찾을 수 없는 상황
            given(userRepository.findById(invalidUserId)).willReturn(Optional.empty());

//          [When & Then]
            assertThatThrownBy(()-> clearRecordService.getClearRecordSummaryByUserId(invalidUserId, page, size))
                    .isInstanceOf(EntityNotFoundException.class)
                    .hasMessage("사용자를 찾을 수 없습니다.");

//          조회 메소드가 실행되었는지 검증
            verify(clearRecordRepository, times(0)).findAllByUserOrderByClearDateDesc(any(), any());

        }

    }
}
