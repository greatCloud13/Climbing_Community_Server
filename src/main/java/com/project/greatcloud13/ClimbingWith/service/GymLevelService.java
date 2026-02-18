package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.GymLevelCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymLevelUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import com.project.greatcloud13.ClimbingWith.repository.GymLevelRepository;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GymLevelService {

    private final GymLevelRepository gymLevelRepository;
    private final GymRepository gymRepository;

    @Transactional
    public GymLevel createGymLevel(GymLevelCreateDTO request){
        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(()-> new EntityNotFoundException("암장을 찾을 수 없습니다"));

        GymLevel gymLevel = GymLevel.builder()
                .gym(gym)
                .displayOrder(request.getDisplayOrder())
                .levelName(request.getLevelName())
                .colorCode(request.getColorCode())
                .description(request.getDescription())
                .build();

        return gymLevelRepository.save(gymLevel);
    }

    @Transactional(readOnly = true)
    public GymLevel getGymLevel(Long id){

        return gymLevelRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("레벨을 찾을 수 없습니다"));
    }

    @Transactional(readOnly = true)
    public List<GymLevel> getAllGymLevelByGym(Long gymId){
        return gymLevelRepository.findAllByGym_Id(gymId);
    }

    @Transactional
    public GymLevel updateGymLevel(Long id, GymLevelUpdateDTO request){
        GymLevel gymLevel = gymLevelRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("레벨을 찾을 수 없습니다"));

        gymLevel.updateGymLevel(request.getLevelName(), request.getDisplayOrder(), request.getColorCode(), request.getDescription());

        return gymLevel;
    }

    @Transactional
    public void deleteGymLevel(Long id){
        gymLevelRepository.deleteById(id);
    }




}
