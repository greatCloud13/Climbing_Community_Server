package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.GymLevelCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymLevelDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymLevelUpdateDTO;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.GymLevelService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/level")
@RequiredArgsConstructor
public class GymLevelController {

    private final GymLevelService gymLevelService;

    @PostMapping
    public ResponseEntity<GymLevelDTO> createGymLevel(@RequestBody GymLevelCreateDTO request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        GymLevelDTO result = gymLevelService.createGymLevel(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/gym/{gymId}")
    public ResponseEntity<List<GymLevelDTO>> getGymLevelList(@PathVariable Long gymId) {
        return ResponseEntity.ok(gymLevelService.getAllGymLevelByGym(gymId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymLevelDTO> getGymLevel(@PathVariable Long id) {
        return ResponseEntity.ok(gymLevelService.getGymLevel(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymLevelDTO> updateGymLevel(@PathVariable Long id,
                                                       @RequestBody GymLevelUpdateDTO request,
                                                       @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(gymLevelService.updateGymLevel(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteGymLevel(@PathVariable Long id,
                                                @AuthenticationPrincipal CustomUserDetails userDetails) {
        gymLevelService.deleteGymLevel(id);
        return ResponseEntity.noContent().build();
    }
}
