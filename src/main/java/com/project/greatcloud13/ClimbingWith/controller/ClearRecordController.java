package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.ClearRecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/clearRecord")
@RequiredArgsConstructor
public class ClearRecordController {

    private final ClearRecordService clearRecordService;

    @Operation(
            summary = "완등 기록 작성",
            description = "새로운 완등기록을 작성합니다."
    )
    @PostMapping
    public ResponseEntity<ClearRecordResponseDTO> createClearRecord(@RequestBody ClearRecordCreateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ClearRecordResponseDTO result = clearRecordService.createClearRecord(userDetails.getUserId(), request);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "사용자 ID 기준 완등 기록 조회",
            description = "최신 일자의 완등 기록부터 정렬됩니다 Pageable 파라미터는 페이지번호와 페이지 사이즈만 동작합니다"
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ClearRecordSummaryDTO>> getAllClearRecordSummaryByUserId(
            @PathVariable Long userId,
            Pageable pageable){
        Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByUserId(userId, pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "문제 ID에 해당하는 완등 기록 조회",
            description = "문제 ID를 기준으로 영상 URL이 존재하는 완등 기록을 조회합니다. Pageable 파라미터는 페이지번호와 페이지 사이즈만 동작합니다"
    )
    @GetMapping("/existVideo/problem/{problemId}")
    public ResponseEntity<Page<ClearRecordSummaryDTO>> getExistVideoClearRecordSummaryByProblem(
            @PathVariable Long problemId, Pageable pageable){
        Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByProblemExistVideoUrl(problemId, pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "사용자 ID와 암장 ID에 해당하는 완등 기록 페이지 조회",
            description = "문제 ID를 기준으로 영상 URL이 존재하는 완등 기록을 조회합니다. Pageable 파라미터는 페이지번호와 페이지 사이즈만 동작합니다"
    )
    @GetMapping("/user/{userId}/gym/{gymId}")
    public ResponseEntity<Page<ClearRecordSummaryDTO>> getClearRecordSummaryByUserIdAndGymId(
            @PathVariable Long userId, @PathVariable Long gymId, Pageable pageable){
        Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByUserIdAndGym(userId, gymId, pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "사용자 ID와 세팅 ID에 해당하는 완등 기록 페이지 조회",
            description = "문제 ID를 기준으로 영상 URL이 존재하는 완등 기록을 조회합니다. Pageable 파라미터는 페이지번호와 페이지 사이즈만 동작합니다"
    )
    @GetMapping("/user/{userId}/setting/{settingId}")
    public ResponseEntity<Page<ClearRecordSummaryDTO>> getClearRecordSummaryUserIdAndSettingId(
            @PathVariable Long userId, @PathVariable Long settingId, Pageable pageable){
        Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByUserIdAndSettingId(userId, settingId, pageable.getPageNumber(), pageable.getPageSize());
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "섹터 ID에 해당하는 완등 기록 조회",
            description = "섹터 ID를 기준으로 영상 URL이 존재하는 완등 기록을 조회합니다. Pageable 파라미터는 페이지번호와 페이지 사이즈만 동작합니다"
    )
    @GetMapping("/existVideo/sector/{sectorId}")
    public ResponseEntity<Page<ClearRecordSummaryDTO>> getClearRecordSummaryBySectorExistVideoUrl(
            @PathVariable Long sectorId, Pageable pageable){
        Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryBySectorExistVideoUrl(sectorId, pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "완등 기록 업데이트",
            description = "기존의 완등기록을 갱신합니다."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ClearRecordResponseDTO> updateClearRecord(@PathVariable Long id, @RequestBody ClearRecordUpdateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails){
        ClearRecordResponseDTO result = clearRecordService.updateClearRecord(userDetails.getUserId(), id, request);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "암장별 완등 기록 통계 조회",
            description = "암장 ID와 사용자 ID를 통해 완등 기록 통계를 조회합니다."
    )
    @GetMapping("/stats/user/{userId}/gym/{gymId}")
    public ResponseEntity<ClearRecordStatisticsResponse> getClearRecordStatistics(
            @PathVariable Long userId, @PathVariable Long gymId){
        ClearRecordStatisticsResponse result = clearRecordService.getClearRecordStatistics(userId, gymId);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "완등 기록 삭제",
            description = "ID에 해당하는 완등 기록을 영구적으로 삭제합니다."
    )
    @DeleteMapping
    public ResponseEntity<Boolean> deleteRecord(Long userId, Long id){
        clearRecordService.deleteClearRecord(userId, id);

        return ResponseEntity.ok(true);
    }

}
