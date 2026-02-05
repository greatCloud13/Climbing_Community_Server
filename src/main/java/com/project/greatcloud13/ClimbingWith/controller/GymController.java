package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.GymCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDetailDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.service.GymManagementService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gym")
@RequiredArgsConstructor
@Slf4j
public class GymController {

    private final GymManagementService gymManagementService;

    @PostMapping
    public ResponseEntity<GymDTO> createGym(@RequestBody GymCreateDTO request){

        GymDTO result = GymDTO.from(gymManagementService.createGym(request));

        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<Page<GymDTO>> getGymList(@RequestParam int page, @RequestParam int size ){

        Page<Gym> result = gymManagementService.findAll(page, size);

        Page<GymDTO> mapResult =  result.map(GymDTO::from);

        return ResponseEntity.ok(mapResult);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymDetailDTO> getGymDetail(@PathVariable Long id){

        GymDetailDTO result = gymManagementService.getGymDetail(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymDTO> updateGym(@PathVariable Long id, @RequestBody GymCreateDTO request){

        Gym result = gymManagementService.updateGym(id, request);

        return ResponseEntity.ok(GymDTO.from(result));
    }

}
