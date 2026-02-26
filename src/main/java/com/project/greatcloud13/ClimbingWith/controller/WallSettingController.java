package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import com.project.greatcloud13.ClimbingWith.service.WallSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/setting")
public class WallSettingController {

    private final WallSettingService wallSettingService;

    @GetMapping("/{id}")
    public ResponseEntity<SettingDetailDTO> getSectorDetail(@PathVariable Long id){
        SettingDetailDTO result = wallSettingService.getSettingDetail(id);

        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<SettingDTO> createSetting(@RequestBody SettingCreateDTO request) {
        SettingDTO result = wallSettingService.createSetting(request);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<SettingDTO> updateSetting(@PathVariable Long id, @RequestBody SettingUpdateDTO request){
        SettingDTO result = wallSettingService.updateSetting(id, request);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteSetting(@PathVariable Long id){
        wallSettingService.deleteSetting(id);

        return ResponseEntity.ok(true);
    }

}
