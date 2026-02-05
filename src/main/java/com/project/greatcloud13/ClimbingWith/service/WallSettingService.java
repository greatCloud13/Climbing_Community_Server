package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.SettingCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.SettingDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.SettingUpdateDTO;
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

@Service
@RequiredArgsConstructor
public class WallSettingService {

    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;
    private final WallSettingRepository settingRepository;
    private final ProblemRepository problemRepository;

    @Transactional
    public Setting createSetting(SettingCreateDTO request){
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

        return settingRepository.save(setting);
    }

    @Transactional
    public SettingDetailDTO getSettingDetail(Long SettingId){
        Setting setting = settingRepository.findById(SettingId)
                .orElseThrow(()-> new EntityNotFoundException("세팅을 찾을 수 없습니다."));

        List<Problem> problemList = problemRepository.findAllBySetting(setting);

        return SettingDetailDTO.from(setting, problemList);
    }

    @Transactional
    public Setting updateSetting(Long settingId, SettingUpdateDTO request){
        Setting setting = settingRepository.findById(settingId)
                .orElseThrow(()->new EntityNotFoundException("세팅정보를 찾을 수 없습니다."));

        setting.update(request.getSettingDate(), request.getStartDate(), request.getEndDate());

        return setting;
    }

    @Transactional
    public void deleteSetting(Long id){
        problemRepository.deleteById(id);
    }

}
