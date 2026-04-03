package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.common.ApiResult;
import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.WallSettingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Wall Setting", description = "암장 세팅 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/setting")
public class WallSettingController {

    private final WallSettingService wallSettingService;

    @Operation(
            summary = "세팅 상세 정보 조회",
            description = "세팅 ID를 통해 해당 세팅의 상세 정보(이름, 기간, 포함된 문제 등)를 조회합니다."
    )
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "상세정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "세팅을 찾을 수 없음")
    public ResponseEntity<ApiResult<SettingDetailDTO>> getSectorDetail(@PathVariable Long id){
        SettingDetailDTO result = wallSettingService.getSettingDetail(id);

        return ResponseEntity.ok(ApiResult.ok(result));
    }

    @Operation(
            summary = "암장별 활성화된 세팅 목록 조회",
            description = "특정 암장 ID에 속한 세팅 중, 현재 유효하거나 활성화된 세팅 리스트를 조회합니다."
    )
    @GetMapping("/gym/{id}")
    @ApiResponse(responseCode = "200", description = "상세정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "암장을 찾을 수 없음")
    public ResponseEntity<ApiResult<List<SettingDTO>>> getActiveSettingListByGymId(@PathVariable Long id){
        List<SettingDTO> result = wallSettingService.getActiveSettingListByGymId(id);

        return ResponseEntity.ok(ApiResult.ok(result));
    }

    @Operation(
            summary = "새로운 세팅 생성",
            description = "섹터 ID와 암장 ID를 통해 새로운 세팅을 등록합니다. 세부정보는 등록 이후에 수정을 통해 등록합니다."
    )
    @PostMapping
    @ApiResponse(responseCode = "201", description = "세팅 생성 됨")
    @ApiResponse(responseCode = "403", description = "요청한 사용자의 권한이 없음")
    @ApiResponse(responseCode = "404", description = "유저 혹은 섹터를 찾을 수 없음")
    public ResponseEntity<ApiResult<SettingDTO>> createSetting(@RequestBody SettingCreateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        SettingDTO result = wallSettingService.createSetting(request.getSectorId(), userDetails.getUserId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(ApiResult.ok(result));
    }

    @Operation(
            summary = "세팅 정보 수정",
            description = "세팅 일자와 해당 세팅의 기간을 업데이트 합니다"
    )
    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "세팅 수정 성공")
    @ApiResponse(responseCode = "403", description = "요청한 사용자의 권한이 없음")
    @ApiResponse(responseCode = "404", description = "유저 혹은 세팅을 찾을 수 없음")
    public ResponseEntity<ApiResult<SettingDTO>> updateSetting(@PathVariable Long id, @RequestBody SettingUpdateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails){
        SettingDTO result = wallSettingService.updateSetting(id, request, userDetails.getUserId());

        return ResponseEntity.ok(ApiResult.ok(result));
    }

    @Operation(
            summary = "세팅 삭제",
            description = "세팅 ID에 해당하는 정보를 삭제합니다."
    )
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204", description = "정상적으로 리소스 삭제됨")
    @ApiResponse(responseCode = "403", description = "요청한 사용자의 권한이 없음")
    @ApiResponse(responseCode = "404", description = "유저 혹은 세팅을 찾을 수 없음")
    public ResponseEntity<ApiResult<Boolean>> deleteSetting(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails){
        wallSettingService.deleteSetting(id, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }
}
