package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.SectorCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Sector;
import com.project.greatcloud13.ClimbingWith.service.SectorManagementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sector")
public class SectorController {

    private final SectorManagementService sectorManagementService;

    @PostMapping
    public ResponseEntity<Sector> createSector(SectorCreateDTO request){
        Sector result = sectorManagementService.createSector(request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/list/{gymId}")
    public ResponseEntity<List<Sector>> getSectorList(@PathVariable Long gymId){
        List<Sector> list = sectorManagementService.findAllSectorByGym(gymId);

        return ResponseEntity.ok(list);
    }

    @PutMapping("/{sectorId}")
    public ResponseEntity<Sector> updateSector(@PathVariable Long sectorId, @RequestBody SectorUpdateDTO request){
        Sector sector = sectorManagementService.updateSector(sectorId, request);

        return ResponseEntity.ok(sector);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectorDetailDTO> getSectorDetail(@PathVariable Long id){

        SectorDetailDTO result = sectorManagementService.getSectorDetail(id);

        return ResponseEntity.ok(result);
    }



}
