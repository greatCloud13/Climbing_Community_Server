package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.exception.BusinessException;
import com.project.greatcloud13.ClimbingWith.common.ErrorCode;
import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Sector;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.sector.SectorNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SectorManagementService {

    private final SectorRepository sectorRepository;
    private final GymRepository gymRepository;
    private final WallSettingRepository settingRepository;
    private final UserRepository userRepository;

    /**
     * 새로운 섹터를 등록합니다.
     * * [Business Rule]
     * 1. 등록하려는 암장(Gym), 사용자(User)가 존재해야 합니다.
     * 2. 요청 유저는 해당 암장에 대한 관리 권한이 있어야 합니다.
     * 3. 초기 등록 시 세팅 일정(settingDate, nextSettingDate)은 비어있는 상태로 생성됩니다.
     *
     * @param request 섹터 생성 정보 (암장 ID, 섹터명 포함)
     * @param userId  요청을 시도하는 유저의 ID
     * @return 생성된 섹터의 DTO
     * @throws UserNotFoundException 유저가 없는 경우
     * @throws GymNotFoundException 암장이 없는 경우
     * @throws GymAccessDeniedException 유저가 요청한 암장에 대한 권한이 없는 경우
     */
    @Transactional
    public SectorDTO createSector(SectorCreateDTO request, Long userId){

//      1. 데이터 존재 여부 검증
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(GymNotFoundException::new);

//      2. 권한 검증
        if(!user.gymValidate(gym)){
            throw new GymAccessDeniedException();
        }

//      3. 도메인 객체 생성 및 저장
        Sector sector = Sector.builder()
                .gym(gym)
                .sectorName(request.getSectorName())
                .settingDate(null)
                .nextSettingDate(null)
                .build();

        return SectorDTO.from(sectorRepository.save(sector));
    }


    /**
     * 섹터의 상세정보를 조회합니다
     * [Business Rule]
     * 1. 조회하려는 섹터가 존재하여야합니다
     *
     * @param id 조회를 시도하는 섹터 ID
     * @return 요청한 ID의 섹터 상세정보
     * @exception SectorNotFoundException 조회하려는 섹터가 없을 경우
     */
    @Transactional(readOnly = true)
    public SectorDetailDTO  getSectorDetail(Long id){


        Sector sector = sectorRepository.findById(id)
                .orElseThrow(SectorNotFoundException::new);

        List<SettingDTO> settingList = settingRepository.findAllBySector(sector).stream().map(SettingDTO :: from).toList();

        return SectorDetailDTO.from(sector, settingList);
    }

    /**
     * 섹터의 상세정보를 갱신합니다.
     * [Business Rule]
     * 1. 갱신하려는 사용자, 섹터가 존재해야 합니다.
     * 2. 갱신을 시도하는 유저는 해당 담장에 대한 관리 권한이 있어야 합니다.
     * 3. 요청 유저는 해당 암장에 대한 관리 권한(소속 여부 등)이 있어야 합니다.
     *
     * @param id 갱신을 시도하는 섹터 ID
     * @param request 요청한 섹터의 갱신할 정보
     * @param userId 갱신을 시도하는 사용자 ID
     * @return 갱신된 Sector DTO
     * @throws UserNotFoundException 유저가 없는 경우
     * @exception SectorNotFoundException 암장이 없는 경우
     * @exception GymAccessDeniedException 유저가 요청한 암장에 대한 권한이 없는 경우
     */
    @Transactional
    public SectorDTO updateSector(Long id, SectorUpdateDTO request, Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Sector sector = sectorRepository.findById(id)
                .orElseThrow(SectorNotFoundException::new);

        if(!user.gymValidate(sector.getGym())){
            throw new GymAccessDeniedException();
        }

        sector.update(request.getSectorName(), request.getSettingDate(), request.getNextSettingDate());

        return SectorDTO.from(sector);
    }

    /**
     * 요청한 암장의 모든 섹터를 조회합니다.
     * [Business Rule]
     * 요청하는 ID에 대한 암장이 존재해야 합니다.
     *
     * @param gymId 조회를 시도하는 암장 ID
     * @return 해당하는 암장의 섹터 리스트
     * @throws GymNotFoundException 암장이 없는 경우
     */
    @Transactional(readOnly = true)
    public List<SectorDTO> findAllSectorByGym(Long gymId){

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

        return sectorRepository.findAllByGym(gym).stream().map(SectorDTO :: from).toList();
    }
}
