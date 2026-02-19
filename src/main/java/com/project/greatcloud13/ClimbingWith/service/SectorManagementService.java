package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.*;
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
    public SectorDTO createSector(SectorCreateDTO request){

        Gym gym = gymRepository.findById(request.getGymId())
                .orElseThrow(()->new EntityNotFoundException("암장을 찾을 수 없습니다."));

        Sector sector = Sector.builder()
                .gym(gym)
                .sectorName(request.getSectorName())
                .settingDate(null)
                .nextSettingDate(null)
                .build();

        return SectorDTO.from(sectorRepository.save(sector));
    }

    @Transactional(readOnly = true)
    public SectorDetailDTO  getSectorDetail(Long id){

        Sector sector = sectorRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("섹터를 찾을 수 없습니다."));

        List<SettingDTO> settingList = settingRepository.findAllBySector(sector).stream().map(SettingDTO :: from).toList();

        return SectorDetailDTO.from(sector, settingList);
    }

    @Transactional
    public SectorDTO updateSector(Long id, SectorUpdateDTO request){

        Sector sector = sectorRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("섹터를 찾을 수 없습니다."));

        sector.update(request.getSectorName(), request.getSettingDate(), request.getNextSettingDate());

        return SectorDTO.from(sector);
    }

    @Transactional(readOnly = true)
    public List<SectorDTO> findAllSectorByGym(Long gymId){

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(()->new EntityNotFoundException("암장을 찾을 수 없습니다"));

        return sectorRepository.findAllByGym(gym).stream().map(SectorDTO :: from).toList();
    }

    @Transactional
    public SectorDTO disableSector(Long id){

        Sector sector = sectorRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("섹터를 찾을 수 없습니다."));

        sector.disable();

        return SectorDTO.from(sector);
    }

    @Transactional
    public SectorDTO enableSector(Long id){

        Sector sector = sectorRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("섹터를 찾을 수 없습니다."));

        sector.enable();

        return SectorDTO.from(sector);
    }
}
