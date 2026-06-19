package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.SettingDTO;
import com.project.greatcloud13.ClimbingWith.dto.SettingDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.SettingUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.sector.SectorNotFoundException;
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
public class WallSettingServiceTest {

    @InjectMocks
    private WallSettingService wallSettingService;

    @Mock private GymRepository gymRepository;
    @Mock private SectorRepository sectorRepository;
    @Mock private WallSettingRepository settingRepository;
    @Mock private ProblemRepository problemRepository;
    @Mock private UserRepository userRepository;

//   ========================= Mock Objects =========================
    private Gym mockGym;
    private Sector mockSector;
    private Setting mockSetting;
    private User mockManager;
    private User mockOtherManager;
//    ===============================================================

    Long gymId = 100L;
    Long sectorId = 200L;
    Long settingId = 300L;
    Long managerId = 500L;
    Long otherManagerId = 501L;

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").gymType(Gym.GymType.BOULDER).address("서울시").build();
        ReflectionTestUtils.setField(mockGym, "id", gymId);

        mockSector = Sector.builder().gym(mockGym).sectorName("A섹터").build();
        ReflectionTestUtils.setField(mockSector, "id", sectorId);

        mockSetting = Setting.builder().sector(mockSector).gym(mockGym).build();
        ReflectionTestUtils.setField(mockSetting, "id", settingId);

        mockManager = User.builder().username("manager").email("m@test.com").password("pw").nickname("매니저").build();
        ReflectionTestUtils.setField(mockManager, "id", managerId);
        ReflectionTestUtils.setField(mockManager, "role", Role.GYM_MANAGER);
        ReflectionTestUtils.setField(mockManager, "gym", mockGym);

        mockOtherManager = User.builder().username("other").email("o@test.com").password("pw").nickname("타매니저").build();
        ReflectionTestUtils.setField(mockOtherManager, "id", otherManagerId);
        ReflectionTestUtils.setField(mockOtherManager, "role", Role.GYM_MANAGER);
    }

    @Nested
    @DisplayName("createSetting() 메서드 테스트")
    class CreateSettingTest {

        @Test
        @DisplayName("정상 세팅 생성 성공")
        void createSetting_success() {
            // [Given]
            given(userRepository.findById(managerId)).willReturn(Optional.of(mockManager));
            given(sectorRepository.findById(sectorId)).willReturn(Optional.of(mockSector));
            given(settingRepository.save(any(Setting.class))).willReturn(mockSetting);

            // [When]
            SettingDTO result = wallSettingService.createSetting(sectorId, managerId);

            // [Then]
            assertThat(result).isNotNull();
            verify(settingRepository, times(1)).save(any(Setting.class));
        }

        @Test
        @DisplayName("존재하지 않는 userId → UserNotFoundException")
        void createSetting_userNotFound() {
            // [Given]
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> wallSettingService.createSetting(sectorId, 999L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 sectorId → SectorNotFoundException")
        void createSetting_sectorNotFound() {
            // [Given]
            given(userRepository.findById(managerId)).willReturn(Optional.of(mockManager));
            given(sectorRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> wallSettingService.createSetting(999L, managerId))
                    .isInstanceOf(SectorNotFoundException.class);
        }

        @Test
        @DisplayName("타 암장 매니저 → GymAccessDeniedException")
        void createSetting_accessDenied() {
            // [Given]
            given(userRepository.findById(otherManagerId)).willReturn(Optional.of(mockOtherManager));
            given(sectorRepository.findById(sectorId)).willReturn(Optional.of(mockSector));

            // [When] & [Then]
            assertThatThrownBy(() -> wallSettingService.createSetting(sectorId, otherManagerId))
                    .isInstanceOf(GymAccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("getActiveSettingListByGymId() 메서드 테스트")
    class GetActiveSettingListTest {

        @Test
        @DisplayName("활성 세팅 목록 조회 성공")
        void getActiveSettingList_success() {
            // [Given]
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(settingRepository.findAllByGymAndIsActive(mockGym, true)).willReturn(List.of(mockSetting));

            // [When]
            List<SettingDTO> result = wallSettingService.getActiveSettingListByGymId(gymId);

            // [Then]
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 gymId → GymNotFoundException")
        void getActiveSettingList_gymNotFound() {
            // [Given]
            given(gymRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> wallSettingService.getActiveSettingListByGymId(999L))
                    .isInstanceOf(GymNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getSettingDetail() 메서드 테스트")
    class GetSettingDetailTest {

        @Test
        @DisplayName("세팅 상세 조회 성공")
        void getSettingDetail_success() {
            // [Given]
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting));
            given(problemRepository.findAllBySetting(mockSetting)).willReturn(List.of());

            // [When]
            SettingDetailDTO result = wallSettingService.getSettingDetail(settingId);

            // [Then]
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 settingId → SettingNotFoundException")
        void getSettingDetail_notFound() {
            // [Given]
            given(settingRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> wallSettingService.getSettingDetail(999L))
                    .isInstanceOf(SettingNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateSetting() 메서드 테스트")
    class UpdateSettingTest {

        @Test
        @DisplayName("정상 세팅 수정 성공")
        void updateSetting_success() {
            // [Given]
            SettingUpdateDTO request = new SettingUpdateDTO();
            given(userRepository.findById(managerId)).willReturn(Optional.of(mockManager));
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting));
            given(settingRepository.findTop2ByGymOrderByIdDesc(mockGym)).willReturn(List.of(mockSetting));

            // [When]
            SettingDTO result = wallSettingService.updateSetting(settingId, request, managerId);

            // [Then]
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("타 암장 매니저 → GymAccessDeniedException")
        void updateSetting_accessDenied() {
            // [Given]
            SettingUpdateDTO request = new SettingUpdateDTO();
            given(userRepository.findById(otherManagerId)).willReturn(Optional.of(mockOtherManager));
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting));

            // [When] & [Then]
            assertThatThrownBy(() -> wallSettingService.updateSetting(settingId, request, otherManagerId))
                    .isInstanceOf(GymAccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("deleteSetting() 메서드 테스트")
    class DeleteSettingTest {

        @Test
        @DisplayName("정상 세팅 삭제 성공 - 이전 세팅 활성화")
        void deleteSetting_success() {
            // [Given]
            Setting prevSetting = Setting.builder().sector(mockSector).gym(mockGym).build();
            ReflectionTestUtils.setField(prevSetting, "id", 299L);

            given(userRepository.findById(managerId)).willReturn(Optional.of(mockManager));
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting));
            given(settingRepository.findFirstByGymOrderByIdDesc(mockGym)).willReturn(Optional.of(prevSetting));

            // [When]
            wallSettingService.deleteSetting(settingId, managerId);

            // [Then]
            verify(settingRepository, times(1)).deleteById(settingId);
            assertThat(prevSetting.isActive()).isTrue();
        }

        @Test
        @DisplayName("타 암장 매니저 → GymAccessDeniedException")
        void deleteSetting_accessDenied() {
            // [Given]
            given(userRepository.findById(otherManagerId)).willReturn(Optional.of(mockOtherManager));
            given(settingRepository.findById(settingId)).willReturn(Optional.of(mockSetting));

            // [When] & [Then]
            assertThatThrownBy(() -> wallSettingService.deleteSetting(settingId, otherManagerId))
                    .isInstanceOf(GymAccessDeniedException.class);
        }

        @Test
        @DisplayName("존재하지 않는 settingId → SettingNotFoundException")
        void deleteSetting_notFound() {
            // [Given]
            given(userRepository.findById(managerId)).willReturn(Optional.of(mockManager));
            given(settingRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> wallSettingService.deleteSetting(999L, managerId))
                    .isInstanceOf(SettingNotFoundException.class);
        }
    }
}
