package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.SectorCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Role;
import com.project.greatcloud13.ClimbingWith.entity.Sector;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.sector.SectorNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.SectorRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.repository.WallSettingRepository;
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
public class SectorManagementServiceTest {

    @InjectMocks
    private SectorManagementService sectorManagementService;

    @Mock private SectorRepository sectorRepository;
    @Mock private GymRepository gymRepository;
    @Mock private WallSettingRepository settingRepository;
    @Mock private UserRepository userRepository;

//   ========================= Mock Objects =========================
    private Gym mockGym;
    private Sector mockSector;
    private User mockManager;
    private User mockOtherManager;
//    ===============================================================

    Long gymId = 100L;
    Long sectorId = 200L;
    Long managerId = 500L;
    Long otherManagerId = 501L;

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").gymType(Gym.GymType.BOULDER).address("서울시").build();
        ReflectionTestUtils.setField(mockGym, "id", gymId);

        mockSector = Sector.builder().gym(mockGym).sectorName("A섹터").build();
        ReflectionTestUtils.setField(mockSector, "id", sectorId);

        mockManager = User.builder().username("manager").email("m@test.com").password("pw").nickname("매니저").build();
        ReflectionTestUtils.setField(mockManager, "id", managerId);
        ReflectionTestUtils.setField(mockManager, "role", Role.GYM_MANAGER);
        ReflectionTestUtils.setField(mockManager, "gym", mockGym);

        mockOtherManager = User.builder().username("other").email("o@test.com").password("pw").nickname("타매니저").build();
        ReflectionTestUtils.setField(mockOtherManager, "id", otherManagerId);
        ReflectionTestUtils.setField(mockOtherManager, "role", Role.GYM_MANAGER);
    }

    @Nested
    @DisplayName("createSector() 메서드 테스트")
    class CreateSectorTest {

        @Test
        @DisplayName("정상 섹터 생성 성공")
        void createSector_success() {
            // [Given]
            SectorCreateDTO request = SectorCreateDTO.builder().gymId(gymId).sectorName("A섹터").build();

            given(userRepository.findById(managerId)).willReturn(Optional.of(mockManager));
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(sectorRepository.save(any(Sector.class))).willReturn(mockSector);

            // [When]
            SectorDTO result = sectorManagementService.createSector(request, managerId);

            // [Then]
            assertThat(result).isNotNull();
            verify(sectorRepository, times(1)).save(any(Sector.class));
        }

        @Test
        @DisplayName("존재하지 않는 userId → UserNotFoundException")
        void createSector_userNotFound() {
            // [Given]
            SectorCreateDTO request = SectorCreateDTO.builder().gymId(gymId).build();
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> sectorManagementService.createSector(request, 999L))
                    .isInstanceOf(UserNotFoundException.class);
        }

        @Test
        @DisplayName("존재하지 않는 gymId → GymNotFoundException")
        void createSector_gymNotFound() {
            // [Given]
            SectorCreateDTO request = SectorCreateDTO.builder().gymId(999L).build();
            given(userRepository.findById(managerId)).willReturn(Optional.of(mockManager));
            given(gymRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> sectorManagementService.createSector(request, managerId))
                    .isInstanceOf(GymNotFoundException.class);
        }

        @Test
        @DisplayName("타 암장 매니저 → GymAccessDeniedException")
        void createSector_accessDenied() {
            // [Given]
            SectorCreateDTO request = SectorCreateDTO.builder().gymId(gymId).build();
            given(userRepository.findById(otherManagerId)).willReturn(Optional.of(mockOtherManager));
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));

            // [When] & [Then]
            assertThatThrownBy(() -> sectorManagementService.createSector(request, otherManagerId))
                    .isInstanceOf(GymAccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("getSectorDetail() 메서드 테스트")
    class GetSectorDetailTest {

        @Test
        @DisplayName("섹터 상세 조회 성공")
        void getSectorDetail_success() {
            // [Given]
            given(sectorRepository.findById(sectorId)).willReturn(Optional.of(mockSector));
            given(settingRepository.findAllBySector(mockSector)).willReturn(List.of());

            // [When]
            SectorDetailDTO result = sectorManagementService.getSectorDetail(sectorId);

            // [Then]
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 sectorId → SectorNotFoundException")
        void getSectorDetail_notFound() {
            // [Given]
            given(sectorRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> sectorManagementService.getSectorDetail(999L))
                    .isInstanceOf(SectorNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("updateSector() 메서드 테스트")
    class UpdateSectorTest {

        @Test
        @DisplayName("정상 섹터 수정 성공")
        void updateSector_success() {
            // [Given]
            SectorUpdateDTO request = new SectorUpdateDTO();
            ReflectionTestUtils.setField(request, "sectorName", "B섹터");

            given(userRepository.findById(managerId)).willReturn(Optional.of(mockManager));
            given(sectorRepository.findById(sectorId)).willReturn(Optional.of(mockSector));

            // [When]
            SectorDTO result = sectorManagementService.updateSector(sectorId, request, managerId);

            // [Then]
            assertThat(result.getSectorName()).isEqualTo("B섹터");
        }

        @Test
        @DisplayName("타 암장 매니저 → GymAccessDeniedException")
        void updateSector_accessDenied() {
            // [Given]
            SectorUpdateDTO request = new SectorUpdateDTO();
            given(userRepository.findById(otherManagerId)).willReturn(Optional.of(mockOtherManager));
            given(sectorRepository.findById(sectorId)).willReturn(Optional.of(mockSector));

            // [When] & [Then]
            assertThatThrownBy(() -> sectorManagementService.updateSector(sectorId, request, otherManagerId))
                    .isInstanceOf(GymAccessDeniedException.class);
        }
    }

    @Nested
    @DisplayName("findAllSectorByGym() 메서드 테스트")
    class FindAllSectorByGymTest {

        @Test
        @DisplayName("암장 섹터 목록 조회 성공")
        void findAllSectorByGym_success() {
            // [Given]
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(sectorRepository.findAllByGym(mockGym)).willReturn(List.of(mockSector));

            // [When]
            List<SectorDTO> result = sectorManagementService.findAllSectorByGym(gymId);

            // [Then]
            assertThat(result.size()).isEqualTo(1);
        }

        @Test
        @DisplayName("존재하지 않는 gymId → GymNotFoundException")
        void findAllSectorByGym_gymNotFound() {
            // [Given]
            given(gymRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> sectorManagementService.findAllSectorByGym(999L))
                    .isInstanceOf(GymNotFoundException.class);
        }
    }
}
