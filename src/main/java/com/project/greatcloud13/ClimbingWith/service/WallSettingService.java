package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.sector.SectorNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.setting.SettingNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class WallSettingService {

    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;
    private final WallSettingRepository settingRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 세팅을 등록합니다.
     * * [Business Rule]
     * 1. 등록하려는 섹터(Sector)가 존재해야 합니다.
     * 2. 등록을 시도하는 유저가 존재해야합니다.
     * 3. 요청 유저는 해당 암장에 대한 관리 권한이 있어야 합니다.
     * 4. 초기 등록 시 세팅 일정(settingDate, endDate, startDate)은 비어있는 상태로 생성됩니다.
     *
     * @param sectorId 등록할 세팅이 속한 섹터 ID
     * @param userId 등록을 시도하는 사용자 ID
     * @return 등록된 세팅 객체 DTO
     * @throws UserNotFoundException 유저가 없는 경우
     * @throws SectorNotFoundException 섹터가 없는 경우
     * @throws GymAccessDeniedException 유저가 요청한 암장에 대한 권한이 없는 경우
     */
    @Transactional
    public SettingDTO createSetting(Long sectorId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(SectorNotFoundException::new);

        if (!user.gymValidate(sector.getGym())) {
            throw new GymAccessDeniedException();
        }

        Setting setting = Setting.builder()
                .sector(sector)
                .gym(sector.getGym())
                .settingDate(null)
                .startDate(null)
                .endDate(null)
                .build();

        return SettingDTO.from(settingRepository.save(setting));
    }

    /**
     * 요청한 암장의 현재 활성화된 세팅을 조회합니다.
     * [Business Rule]
     * 1. 조회하는 암장(Gym)이 존재해야 합니다.
     *
     * @param gymId 조회하는 암장 ID
     * @return 현재 활성화된 SettingList
     * @throws GymNotFoundException 암장이 없는 경우
     */
    public List<SettingDTO> getActiveSettingListByGymId(Long gymId) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

        return settingRepository.findAllByGymAndIsActive(gym, true).stream()
                .map(SettingDTO::from)
                .toList();
    }

    /**
     * 요청한 세팅의 상세 정보를 조회합니다.
     * [Business Rule]
     * 1. 조회하는 세팅이 존재해야 합니다.
     *
     * @param settingId 조회하는 세팅 ID
     * @return 요청한 ID에 해당하는 세팅 상세정보
     * @throws SettingNotFoundException 세팅이 없는 경우
     */
    public SettingDetailDTO getSettingDetail(Long settingId) {
        Setting setting = settingRepository.findById(settingId)
                .orElseThrow(SettingNotFoundException::new);

        List<ProblemDTO> problemList = problemRepository.findAllBySetting(setting).stream()
                .map(ProblemDTO::from)
                .toList();

        return SettingDetailDTO.from(setting, problemList);
    }

    /**
     * 요청한 ID의 세팅을 수정합니다.
     * [Business Rule]
     * 1. 수정하는 세팅이 존재해야합니다.
     * 2. 수정을 시도하는 유저가 존재해야합니다.
     * 3. 수정을 시도하는 유저는 해당하는 세팅이 속한 암장에 대한 관리 권한이 있어야 합니다.
     *
     * @param settingId 수정을 시도하는 세팅 ID
     * @param request 해당하는 세팅의 갱신할 정보
     * @param userId 수정을 시도하는 사용자 ID
     * @return 수정된 Setting DTO
     * @throws UserNotFoundException 유저가 없는 경우
     * @throws SettingNotFoundException 세팅이 없는 경우
     * @throws GymAccessDeniedException 유저가 요청한 암장에 대한 권한이 없는 경우
     */
    @Transactional
    public SettingDTO updateSetting(Long settingId, SettingUpdateDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Setting setting = settingRepository.findById(settingId)
                .orElseThrow(SettingNotFoundException::new);

        if (!user.gymValidate(setting.getGym())) {
            throw new GymAccessDeniedException();
        }

        // 새 세팅 등록 시 직전 세팅을 비활성화하여 최신 세팅만 active 상태를 유지
        settingRepository.findTop2ByGymOrderByIdDesc(setting.getGym())
                .stream()
                .skip(1)
                .findFirst()
                .ifPresent(Setting::inActive);

        setting.update(request.getSettingDate(), request.getStartDate(), request.getEndDate());

        return SettingDTO.from(setting);
    }

    /**
     * 요청한 ID의 세팅을 삭제합니다.
     * [Business Rule]
     * 1. 삭제를 시도하는 유저가 존재해야합니다.
     * 2. 삭제하는 대상 세팅이 속한 암장에 대한 관리 권한이 있어야 합니다.
     *
     * @param id 세팅 ID
     * @param userId 요청을 시도하는 사용자 ID
     * @throws UserNotFoundException 유저가 없는 경우
     * @throws SettingNotFoundException 세팅이 없는 경우
     * @throws GymAccessDeniedException 유저가 요청한 암장에 대한 권한이 없는 경우
     */
    @Transactional
    public void deleteSetting(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Setting setting = settingRepository.findById(id)
                .orElseThrow(SettingNotFoundException::new);

        if (!user.gymValidate(setting.getGym())) {
            throw new GymAccessDeniedException();
        }

        Gym gym = setting.getGym();
        settingRepository.deleteById(id);
        settingRepository.findFirstByGymOrderByIdDesc(gym).ifPresent(Setting::active);
    }
}
