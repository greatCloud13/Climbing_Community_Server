package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.Sector;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.SectorRepository;
import com.project.greatcloud13.ClimbingWith.repository.WallSettingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class WallSettingService {

    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;
    private final WallSettingRepository settingRepository;
    private final ProblemRepository problemRepository;

    @Transactional
    public SettingDTO createSetting(SettingCreateDTO request){
        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(()-> new EntityNotFoundException("암장을 찾을 수 없습니다."));

        Sector sector = sectorRepository.findById(request.getSectorId())
                .orElseThrow(()-> new EntityNotFoundException("섹터를 찾을 수 없습니다."));

        Setting setting = Setting.builder()
                .sector(sector)
                .gym(gym)
                .settingDate(null)
                .startDate(null)
                .endDate(null)
                .build();

        return SettingDTO.from(settingRepository.save(setting));
    }

    @Transactional
    public SettingDetailDTO getSettingDetail(Long SettingId){
        Setting setting = settingRepository.findById(SettingId)
                .orElseThrow(()-> new EntityNotFoundException("세팅을 찾을 수 없습니다."));

        List<ProblemDTO> problemList = problemRepository.findAllBySetting(setting).stream().map(ProblemDTO :: from).toList();

        return SettingDetailDTO.from(setting, problemList);
    }

    @Transactional
    public SettingDTO updateSetting(Long settingId, SettingUpdateDTO request){
        Setting setting = settingRepository.findById(settingId)
                .orElseThrow(()->new EntityNotFoundException("세팅정보를 찾을 수 없습니다."));

        settingRepository.findTop2ByGymOrderByIdDesc(setting.getGym())
                .stream()
                .skip(1)
                .findFirst()
                .ifPresent(Setting::inActive);

        setting.update(request.getSettingDate(), request.getStartDate(), request.getEndDate());

        return SettingDTO.from(setting);
    }

    @Transactional
    public void deleteSetting(Long id){
        Setting setting = settingRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("세팅정보를 찾을 수 없습니다."));
        settingRepository.deleteById(id);
        Optional<Setting> pastSetting = settingRepository.findFirstByGymOrderByIdDesc(setting.getGym());
        pastSetting.ifPresent(Setting::active);
    }

}
