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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymLevelService {

    private final GymLevelRepository gymLevelRepository;
    private final GymRepository gymRepository;

    @Transactional
    public GymLevelDTO createGymLevel(GymLevelCreateDTO request) {
        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(GymNotFoundException::new);

        GymLevel gymLevel = GymLevel.builder()
                .gym(gym)
                .displayOrder(request.getDisplayOrder())
                .levelName(request.getLevelName())
                .colorCode(request.getColorCode())
                .description(request.getDescription())
                .build();

        return GymLevelDTO.from(gymLevelRepository.save(gymLevel));
    }

    public GymLevelDTO getGymLevel(Long id) {
        return GymLevelDTO.from(
                gymLevelRepository.findById(id).orElseThrow(GymLevelNotFoundException::new)
        );
    }

    public List<GymLevelDTO> getAllGymLevelByGym(Long gymId) {
        return gymLevelRepository.findAllByGym_Id(gymId).stream()
                .map(GymLevelDTO::from)
                .toList();
    }

    @Transactional
    public GymLevelDTO updateGymLevel(Long id, GymLevelUpdateDTO request) {
        GymLevel gymLevel = gymLevelRepository.findById(id)
                .orElseThrow(GymLevelNotFoundException::new);

        gymLevel.updateGymLevel(request.getLevelName(), request.getDisplayOrder(), request.getColorCode(), request.getDescription());

        return GymLevelDTO.from(gymLevel);
    }

    @Transactional
    public void deleteGymLevel(Long id) {
        if (!gymLevelRepository.existsById(id)) {
            throw new GymLevelNotFoundException();
        }
        gymLevelRepository.deleteById(id);
    }
}
