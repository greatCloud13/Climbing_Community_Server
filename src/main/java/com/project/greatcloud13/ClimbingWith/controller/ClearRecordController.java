package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.*;
import com.project.greatcloud13.ClimbingWith.entity.ClearRecord;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.ClearRecordService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
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
            description = "최신 일자의 완등 기록부터 정렬됩니다"
    )
    @GetMapping("/page/{page}/size/{size}")
    public ResponseEntity<Page<ClearRecordSummaryDTO>> getAllClearRecordSummaryByUserId(
            @PathVariable Long userId, @PathVariable int page, @PathVariable int size){
        Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByUserId(userId, page, size);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "문제 ID에 해당하는 완등 기록 조회",
            description = "문제 ID를 기준으로 영상 URL이 존재하는 완등 기록을 조회합니다."
    )
    @GetMapping("/existVideo/problem/{problemId}/page/{page}/size/{size}")
    public ResponseEntity<Page<ClearRecordSummaryDTO>> getExistVideoClearRecordSummaryByProblem(
            @PathVariable Long problemId, @PathVariable int page, @PathVariable int size){
        Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryByProblemExistVideoUrl(problemId, page, size);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "섹터 ID에 해당하는 완등 기록 조회",
            description = "섹터 ID를 기준으로 영상 URL이 존재하는 완등 기록을 조회합니다."
    )
    @GetMapping("/existVideo/sector/{sectorId}/page/{page}/size/{size}")
    public ResponseEntity<Page<ClearRecordSummaryDTO>> getClearRecordSummaryBySectorExistVideoUrl(
            @PathVariable Long sectorId,@PathVariable int page,@PathVariable int size){
        Page<ClearRecordSummaryDTO> result = clearRecordService.getClearRecordSummaryBySectorExistVideoUrl(sectorId, page, size);

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
            summary = "완등 기록 삭제",
            description = "ID에 해당하는 완등 기록을 영구적으로 삭제합니다."
    )
    @PutMapping
    public ResponseEntity<Boolean> deleteRecord(Long userId, Long id){
        clearRecordService.deleteClearRecord(userId, id);

        return ResponseEntity.ok(true);
    }

}
