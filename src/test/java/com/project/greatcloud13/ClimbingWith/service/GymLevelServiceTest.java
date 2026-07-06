package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.GymLevelCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymLevelDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymLevelUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymLevelNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymLevelRepository;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
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
public class GymLevelServiceTest {

    @InjectMocks
    private GymLevelService gymLevelService;

    @Mock private GymLevelRepository gymLevelRepository;
    @Mock private GymRepository gymRepository;

//   ========================= Mock Objects =========================
    private Gym mockGym;
    private GymLevel mockGymLevel;
//    ===============================================================

    Long gymId = 100L;
    Long levelId = 200L;

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").gymType(Gym.GymType.BOULDER).address("서울시").build();
        ReflectionTestUtils.setField(mockGym, "id", gymId);

        mockGymLevel = GymLevel.builder()
                .gym(mockGym)
                .levelName("초급")
                .displayOrder(1)
                .colorCode("#FF0000")
                .description("초급자용 코스")
                .build();
        ReflectionTestUtils.setField(mockGymLevel, "id", levelId);
    }

    @Nested
    @DisplayName("createGymLevel() 메서드 테스트")
    class CreateGymLevelTest {

        @Test
        @DisplayName("레벨 생성 성공")
        void createGymLevel_success() {
            // [Given]
            GymLevelCreateDTO request = new GymLevelCreateDTO();
            ReflectionTestUtils.setField(request, "gymId", gymId);
            ReflectionTestUtils.setField(request, "levelName", "초급");
            ReflectionTestUtils.setField(request, "displayOrder", 1);
            ReflectionTestUtils.setField(request, "colorCode", "#FF0000");
            ReflectionTestUtils.setField(request, "description", "초급자용");

            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(gymLevelRepository.save(any(GymLevel.class))).willReturn(mockGymLevel);

            // [When]
            GymLevelDTO result = gymLevelService.createGymLevel(request);

            // [Then]
            assertThat(result).isNotNull();
            assertThat(result.getLevelName()).isEqualTo("초급");
            verify(gymLevelRepository, times(1)).save(any(GymLevel.class));
        }

        @Test
        @DisplayName("존재하지 않는 gymId → GymNotFoundException")
        void createGymLevel_gymNotFound() {
            // [Given]
            GymLevelCreateDTO request = new GymLevelCreateDTO();
            ReflectionTestUtils.setField(request, "gymId", 999L);
            given(gymRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> gymLevelService.createGymLevel(request))
                    .isInstanceOf(GymNotFoundException.class);

            verify(gymLevelRepository, never()).save(any());
        }
    }

    @Nested
    @DisplayName("getGymLevel() 메서드 테스트")
    class GetGymLevelTest {

        @Test
        @DisplayName("레벨 단건 조회 성공")
        void getGymLevel_success() {
            // [Given]
            given(gymLevelRepository.findById(levelId)).willReturn(Optional.of(mockGymLevel));

            // [When]
            GymLevelDTO result = gymLevelService.getGymLevel(levelId);

            // [Then]
            assertThat(result.getLevelName()).isEqualTo("초급");
        }

        @Test
        @DisplayName("존재하지 않는 id → GymLevelNotFoundException")
        void getGymLevel_notFound() {
            // [Given]
            given(gymLevelRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> gymLevelService.getGymLevel(999L))
                    .isInstanceOf(GymLevelNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getAllGymLevelByGym() 메서드 테스트")
    class GetAllGymLevelByGymTest {

        @Test
        @DisplayName("암장 레벨 목록 조회 성공")
        void getAllGymLevelByGym_success() {
            // [Given]
            given(gymLevelRepository.findAllByGym_Id(gymId)).willReturn(List.of(mockGymLevel));

            // [When]
            List<GymLevelDTO> result = gymLevelService.getAllGymLevelByGym(gymId);

            // [Then]
            assertThat(result.size()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("updateGymLevel() 메서드 테스트")
    class UpdateGymLevelTest {

        @Test
        @DisplayName("레벨 수정 성공")
        void updateGymLevel_success() {
            // [Given]
            GymLevelUpdateDTO request = new GymLevelUpdateDTO();
            ReflectionTestUtils.setField(request, "levelName", "중급");
            ReflectionTestUtils.setField(request, "displayOrder", 2);
            ReflectionTestUtils.setField(request, "colorCode", "#0000FF");
            ReflectionTestUtils.setField(request, "description", "중급자용");

            given(gymLevelRepository.findById(levelId)).willReturn(Optional.of(mockGymLevel));

            // [When]
            GymLevelDTO result = gymLevelService.updateGymLevel(levelId, request);

            // [Then]
            assertThat(result.getLevelName()).isEqualTo("중급");
        }

        @Test
        @DisplayName("존재하지 않는 id → GymLevelNotFoundException")
        void updateGymLevel_notFound() {
            // [Given]
            GymLevelUpdateDTO request = new GymLevelUpdateDTO();
            given(gymLevelRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> gymLevelService.updateGymLevel(999L, request))
                    .isInstanceOf(GymLevelNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("deleteGymLevel() 메서드 테스트")
    class DeleteGymLevelTest {

        @Test
        @DisplayName("레벨 삭제 성공")
        void deleteGymLevel_success() {
            // [Given]
            given(gymLevelRepository.existsById(levelId)).willReturn(true);

            // [When]
            gymLevelService.deleteGymLevel(levelId);

            // [Then]
            verify(gymLevelRepository, times(1)).deleteById(levelId);
        }

        @Test
        @DisplayName("존재하지 않는 id → GymLevelNotFoundException")
        void deleteGymLevel_notFound() {
            // [Given]
            given(gymLevelRepository.existsById(999L)).willReturn(false);

            // [When] & [Then]
            assertThatThrownBy(() -> gymLevelService.deleteGymLevel(999L))
                    .isInstanceOf(GymLevelNotFoundException.class);

            verify(gymLevelRepository, never()).deleteById(any());
        }
    }
}
