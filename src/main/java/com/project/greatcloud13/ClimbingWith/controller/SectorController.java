package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.SectorCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorDTO;
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
    public ResponseEntity<SectorDTO> createSector(SectorCreateDTO request){
        SectorDTO result = sectorManagementService.createSector(request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/list/{gymId}")
    public ResponseEntity<List<SectorDTO>> getSectorList(@PathVariable Long gymId){
        List<SectorDTO> list = sectorManagementService.findAllSectorByGym(gymId);

        return ResponseEntity.ok(list);
    }

    @PutMapping("/{sectorId}")
    public ResponseEntity<SectorDTO> updateSector(@PathVariable Long sectorId, @RequestBody SectorUpdateDTO request){
        SectorDTO sector = sectorManagementService.updateSector(sectorId, request);

        return ResponseEntity.ok(sector);
    }

    @GetMapping("/{id}")
    public ResponseEntity<SectorDetailDTO> getSectorDetail(@PathVariable Long id){

        SectorDetailDTO result = sectorManagementService.getSectorDetail(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/enable/{id}")
    public ResponseEntity<SectorDTO> enableSector(@PathVariable Long id){
        SectorDTO result = sectorManagementService.enableSector(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/disable/{id}")
    public ResponseEntity<SectorDTO> disableSector(@PathVariable Long id){
        SectorDTO result = sectorManagementService.disableSector(id);

        return ResponseEntity.ok(result);
    }

}
