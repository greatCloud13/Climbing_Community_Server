package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.GymCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymSearchRequest;
import com.project.greatcloud13.ClimbingWith.dto.GymUpdateDTO;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.GymManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
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

    @Operation(
            summary = "새로운 암장 생성",
            description = "관리자가 새로운 암장을 등록합니다."
    )
    @PostMapping
    @ApiResponse(responseCode = "201", description = "생성됨")
    @ApiResponse(responseCode = "400", description = "소개 사진 개수 초과 등 요청 값이 유효하지 않음")
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음")
    @ApiResponse(responseCode = "404", description = "사용자가 존재하지 않음")
    public ResponseEntity<GymDTO> createGym(@RequestBody @Valid GymCreateDTO request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        GymDTO result = gymManagementService.createGym(request, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "암장 목록 조회",
            description = "등록된 암장 목록을 생성일 기준 최신순으로 페이징 조회합니다."
    )
    @GetMapping
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<Page<GymDTO>> getGymList(@RequestParam int page, @RequestParam int size) {
        return ResponseEntity.ok(gymManagementService.findAll(page, size));
    }

    @Operation(
            summary = "암장 검색",
            description = "이름·주소 키워드, 암장 타입, 해시태그, 활성 여부 조건으로 암장을 검색합니다. 조건을 지정하지 않으면 활성화된 암장 전체를 반환합니다."
    )
    @GetMapping("/search")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    public ResponseEntity<Page<GymDTO>> searchGym(@ModelAttribute GymSearchRequest request) {
        return ResponseEntity.ok(gymManagementService.search(request));
    }

    @Operation(
            summary = "암장 상세 조회",
            description = "암장 ID를 통해 해당 암장의 상세 정보와 소속 섹터 목록을 조회합니다."
    )
    @GetMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "암장이 존재하지 않음")
    public ResponseEntity<GymDetailDTO> getGymDetail(@PathVariable Long id) {
        return ResponseEntity.ok(gymManagementService.getGymDetail(id));
    }

    @Operation(
            summary = "암장 정보 수정",
            description = "관리자가 암장의 이름, 주소, 운영시간 등 기존 정보를 업데이트합니다."
    )
    @PutMapping("/{id}")
    @ApiResponse(responseCode = "200", description = "업데이트 성공")
    @ApiResponse(responseCode = "400", description = "소개 사진 개수 초과 등 요청 값이 유효하지 않음")
    @ApiResponse(responseCode = "403", description = "관리자 권한이 없음")
    @ApiResponse(responseCode = "404", description = "사용자 혹은 암장이 존재하지 않음")
    public ResponseEntity<GymDTO> updateGym(@PathVariable Long id,
                                             @RequestBody @Valid GymUpdateDTO request,
                                             @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(gymManagementService.updateGym(id, request, userDetails.getUserId()));
    }
}
