package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.GymLevelCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymLevelUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import com.project.greatcloud13.ClimbingWith.service.GymLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/level")
@RequiredArgsConstructor
public class GymLevelController {

    private final GymLevelService gymLevelService;

    @PostMapping
    public ResponseEntity<GymLevel> createGymLevel(@RequestBody GymLevelCreateDTO request){
        GymLevel result = gymLevelService.createGymLevel(request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/gym/{gymId}")
    public ResponseEntity<List<GymLevel>> getGymLevelList(@PathVariable Long gymId){
        List<GymLevel> result = gymLevelService.getAllGymLevelByGym(gymId);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymLevel> getGymLevel(@PathVariable Long id){
        GymLevel result = gymLevelService.getGymLevel(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymLevel> updateGymLevel(@PathVariable Long id, @RequestBody GymLevelUpdateDTO request){
        GymLevel result = gymLevelService.updateGymLevel(id, request);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteGymLevel(@PathVariable Long id){
        gymLevelService.deleteGymLevel(id);

        return ResponseEntity.ok(true);
    }

}
