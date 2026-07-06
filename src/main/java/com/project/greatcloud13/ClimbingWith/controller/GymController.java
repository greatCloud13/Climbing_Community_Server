package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.GymCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymUpdateDTO;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.GymManagementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/gym")
@RequiredArgsConstructor
@Slf4j
public class GymController {

    private final GymManagementService gymManagementService;

    @PostMapping
    public ResponseEntity<GymDTO> createGym(@RequestBody @Valid GymCreateDTO request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        GymDTO result = gymManagementService.createGym(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping
    public ResponseEntity<Page<GymDTO>> getGymList(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(gymManagementService.findAll(page, size));
    }

    @GetMapping("/{id}")
    public ResponseEntity<GymDetailDTO> getGymDetail(@PathVariable Long id) {
        return ResponseEntity.ok(gymManagementService.getGymDetail(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<GymDTO> updateGym(@PathVariable Long id,
                                             @RequestBody @Valid GymUpdateDTO request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(gymManagementService.updateGym(id, request, userDetails.getUserId()));
    }
}
