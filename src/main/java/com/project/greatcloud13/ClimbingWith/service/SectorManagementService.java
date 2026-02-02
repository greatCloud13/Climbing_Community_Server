package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.SectorCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
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
public class SectorManagementService {

    private final SectorRepository sectorRepository;
    private final GymRepository gymRepository;
    private final WallSettingRepository settingRepository;

    @Transactional
    public Sector createSector(SectorCreateDTO request){

        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(()->new EntityNotFoundException("암장을 찾을 수 없습니다."));

        Sector sector = Sector.builder()
                .gym(gym)
                .sectorName(request.getSectorName())
                .settingDate(null)
                .nextSettingDate(null)
                .build();

        return sectorRepository.save(sector);
    }

    @Transactional(readOnly = true)
    public SectorDetailDTO getSectorDetail(Long id){

        Sector sector = sectorRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("섹터를 찾을 수 없습니다."));

        List<Setting> settingList = settingRepository.findAllBySector(sector);

        return SectorDetailDTO.from(sector, settingList);
    }

    @Transactional
    public Sector updateSector(Long id, SectorUpdateDTO request){

        Sector sector = sectorRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("섹터를 찾을 수 없습니다."));

        sector.update(request.getSectorName(), request.getSettingDate(), request.getNextSettingDate());

        return sector;
    }

    @Transactional(readOnly = true)
    public List<Sector> findAllSectorByGym(Long gymId){

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(()->new EntityNotFoundException("암장을 찾을 수 없습니다"));

        return sectorRepository.findAllByGym(gym);
    }
}
