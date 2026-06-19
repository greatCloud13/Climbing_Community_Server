package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.GymCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Role;
import com.project.greatcloud13.ClimbingWith.entity.Sector;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.common.AccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.SectorRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
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
public class GymManagementServiceTest {

    @InjectMocks
    private GymManagementService gymManagementService;

    @Mock private GymRepository gymRepository;
    @Mock private SectorRepository sectorRepository;
    @Mock private UserRepository userRepository;

//   ========================= Mock Objects =========================
    private User mockAdmin;
    private User mockMember;
    private Gym mockGym;
//    ===============================================================

    Long gymId = 100L;
    Long adminId = 1L;
    Long memberId = 2L;

    @BeforeEach
    void setUp() {
        mockGym = Gym.builder().gymName("테스트 암장").gymType(Gym.GymType.BOULDER).address("서울시").build();
        ReflectionTestUtils.setField(mockGym, "id", gymId);

        mockAdmin = User.builder().username("admin").email("admin@test.com").password("pw").nickname("관리자").build();
        ReflectionTestUtils.setField(mockAdmin, "id", adminId);
        ReflectionTestUtils.setField(mockAdmin, "role", Role.ADMIN);

        mockMember = User.builder().username("member").email("member@test.com").password("pw").nickname("일반유저").build();
        ReflectionTestUtils.setField(mockMember, "id", memberId);
        ReflectionTestUtils.setField(mockMember, "role", Role.MEMBER);
    }

    @Nested
    @DisplayName("createGym() 메서드 테스트")
    class CreateGymTest {

        @Test
        @DisplayName("ADMIN 유저 암장 생성 성공")
        void createGym_success() {
            // [Given]
            GymCreateDTO request = new GymCreateDTO("새 암장", Gym.GymType.BOULDER, "서울시 강남구", null, null, null, null, null);
            given(userRepository.findById(adminId)).willReturn(Optional.of(mockAdmin));
            given(gymRepository.save(any(Gym.class))).willReturn(mockGym);

            // [When]
            GymDTO result = gymManagementService.createGym(request, adminId);

            // [Then]
            assertThat(result).isNotNull();
            verify(gymRepository, times(1)).save(any(Gym.class));
        }

        @Test
        @DisplayName("ADMIN이 아닌 유저 → AccessDeniedException")
        void createGym_notAdmin() {
            // [Given]
            GymCreateDTO request = new GymCreateDTO("새 암장", Gym.GymType.BOULDER, "서울시 강남구", null, null, null, null, null);
            given(userRepository.findById(memberId)).willReturn(Optional.of(mockMember));

            // [When] & [Then]
            assertThatThrownBy(() -> gymManagementService.createGym(request, memberId))
                    .isInstanceOf(AccessDeniedException.class);

            verify(gymRepository, never()).save(any());
        }

        @Test
        @DisplayName("존재하지 않는 userId → UserNotFoundException")
        void createGym_userNotFound() {
            // [Given]
            GymCreateDTO request = new GymCreateDTO("새 암장", Gym.GymType.BOULDER, "서울시 강남구", null, null, null, null, null);
            given(userRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> gymManagementService.createGym(request, 999L))
                    .isInstanceOf(UserNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findAll() 메서드 테스트")
    class FindAllTest {

        @Test
        @DisplayName("암장 목록 페이지 조회 성공")
        void findAll_success() {
            // [Given]
            Page<Gym> mockPage = new PageImpl<>(List.of(mockGym), PageRequest.of(0, 10), 1);
            given(gymRepository.findAll(any(org.springframework.data.domain.Pageable.class))).willReturn(mockPage);

            // [When]
            Page<GymDTO> result = gymManagementService.findAll(0, 10);

            // [Then]
            assertThat(result.getTotalElements()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("updateGym() 메서드 테스트")
    class UpdateGymTest {

        @Test
        @DisplayName("ADMIN 유저 암장 수정 성공")
        void updateGym_success() {
            // [Given]
            GymUpdateDTO request = new GymUpdateDTO();
            ReflectionTestUtils.setField(request, "gymName", "수정된 암장");
            ReflectionTestUtils.setField(request, "address", "부산시");
            given(userRepository.findById(adminId)).willReturn(Optional.of(mockAdmin));
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));

            // [When]
            GymDTO result = gymManagementService.updateGym(gymId, request, adminId);

            // [Then]
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("ADMIN이 아닌 유저 → AccessDeniedException")
        void updateGym_notAdmin() {
            // [Given]
            GymUpdateDTO request = new GymUpdateDTO();
            given(userRepository.findById(memberId)).willReturn(Optional.of(mockMember));

            // [When] & [Then]
            assertThatThrownBy(() -> gymManagementService.updateGym(gymId, request, memberId))
                    .isInstanceOf(AccessDeniedException.class);
        }

        @Test
        @DisplayName("존재하지 않는 gymId → GymNotFoundException")
        void updateGym_gymNotFound() {
            // [Given]
            GymUpdateDTO request = new GymUpdateDTO();
            given(userRepository.findById(adminId)).willReturn(Optional.of(mockAdmin));
            given(gymRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> gymManagementService.updateGym(999L, request, adminId))
                    .isInstanceOf(GymNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("getGymDetail() 메서드 테스트")
    class GetGymDetailTest {

        @Test
        @DisplayName("암장 상세 조회 성공")
        void getGymDetail_success() {
            // [Given]
            given(gymRepository.findById(gymId)).willReturn(Optional.of(mockGym));
            given(sectorRepository.findAllByGym(mockGym)).willReturn(List.of());

            // [When]
            GymDetailDTO result = gymManagementService.getGymDetail(gymId);

            // [Then]
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 gymId → GymNotFoundException")
        void getGymDetail_notFound() {
            // [Given]
            given(gymRepository.findById(999L)).willReturn(Optional.empty());

            // [When] & [Then]
            assertThatThrownBy(() -> gymManagementService.getGymDetail(999L))
                    .isInstanceOf(GymNotFoundException.class);
        }
    }
}
