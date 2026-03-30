package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.common.ApiResult;
import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.SectorManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Tag(name = "Sector", description = "암장 내 구역(섹터) 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sector")
public class SectorController {

    private final SectorManagementService sectorManagementService;

    @Operation(
            summary = "새로운 섹터 생성",
            description = "암장 내에 새로운 구역(섹터)을 등록합니다. 생성 후 Location 헤더를 통해 상세 주소를 제공합니다."
    )
    @PostMapping
    @ApiResponse(responseCode = "201", description = "생성됨")
    @ApiResponse(responseCode = "403", description = "요청한 사용자의 권한이 없음")
    @ApiResponse(responseCode = "404", description = "사용자 혹은 암장이 존재하지 않음")
    public ResponseEntity<ApiResult<SectorDTO>> createSector(@RequestBody SectorCreateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails){
        SectorDTO result = sectorManagementService.createSector(request, userDetails.getUserId());

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .build()
                .toUri();

        return ResponseEntity.created(location).body(ApiResult.ok(result));
    }

    @Operation(
            summary = "암장별 섹터 목록 조회",
            description = "특정 암장 ID에 포함된 모든 섹터 리스트를 조회합니다."
    )
    @GetMapping("/list/{gymId}")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "암장이 존재하지 않음")
    public ResponseEntity<ApiResult<List<SectorDTO>>> getSectorList(@PathVariable Long gymId){
        List<SectorDTO> list = sectorManagementService.findAllSectorByGym(gymId);

        return ResponseEntity.ok(ApiResult.ok(list));
    }

    @Operation(
            summary = "섹터 정보 수정",
            description = "섹터의 이름이나 설명 등 기존 정보를 업데이트합니다."
    )
    @PutMapping("/{sectorId}")
    @ApiResponse(responseCode = "200", description = "업데이트 성공")
    @ApiResponse(responseCode = "403", description = "요청한 사용자의 권한이 없음")
    @ApiResponse(responseCode = "404", description = "사용자 혹은 암장이 존재하지 않음")
    public ResponseEntity<ApiResult<SectorDTO>> updateSector(@PathVariable Long sectorId, @RequestBody SectorUpdateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails){
        SectorDTO sector = sectorManagementService.updateSector(sectorId, request, userDetails.getUserId());

        return ResponseEntity.ok(ApiResult.ok(sector));
    }

    @Operation(
            summary = "섹터 상세 정보 조회",
            description = "섹터 ID를 통해 해당 섹터의 상세 정보와 포함된 문제 목록 등을 조회합니다."
    )
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "상세정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "섹터를 찾을 수 없음")
    public ResponseEntity<ApiResult<SectorDetailDTO>> getSectorDetail(@PathVariable Long id){

        SectorDetailDTO result = sectorManagementService.getSectorDetail(id);

        return ResponseEntity.ok(ApiResult.ok(result));
    }



}
