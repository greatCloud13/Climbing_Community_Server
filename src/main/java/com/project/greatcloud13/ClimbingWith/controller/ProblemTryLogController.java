package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.ProblemTryLogDTO;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.ProblemTryLogService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/problemTryLog")
@RequiredArgsConstructor
public class ProblemTryLogController {

    private final ProblemTryLogService problemTryLogService;

    @Operation(
            summary = "문제 시도 기록 생성",
            description = "로그인한 사용자의 문제 시도 기록을 생성합니다."
    )
    @PostMapping
    public ResponseEntity<ProblemTryLogDTO> createProblemTryLog(
            @RequestBody ProblemTryLogDTO request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProblemTryLogDTO result = problemTryLogService.createProblemTryLog(userDetails.getUserId(), request);

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @Operation(
            summary = "문제 시도 기록 단건 조회",
            description = "ID에 해당하는 문제 시도 기록을 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<ProblemTryLogDTO> getProblemTryLog(@PathVariable Long id) {
        ProblemTryLogDTO result = problemTryLogService.getProblemTryLog(id);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "사용자별 문제 시도 기록 목록 조회",
            description = "최신 시도 일자 순으로 정렬됩니다."
    )
    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<ProblemTryLogDTO>> getProblemTryLogsByUser(
            @PathVariable Long userId, @ParameterObject Pageable pageable) {
        Page<ProblemTryLogDTO> result = problemTryLogService.getProblemTryLogsByUser(userId, pageable.getPageNumber(), pageable.getPageSize());

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "문제 시도 기록 수정",
            description = "작성자 본인의 문제 시도 기록을 수정합니다."
    )
    @PutMapping("/{id}")
    public ResponseEntity<ProblemTryLogDTO> updateProblemTryLog(
            @PathVariable Long id, @RequestBody ProblemTryLogDTO request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProblemTryLogDTO result = problemTryLogService.updateProblemTryLog(id, userDetails.getUserId(), request);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "문제 시도 기록 삭제",
            description = "작성자 본인의 문제 시도 기록을 삭제합니다."
    )
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblemTryLog(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        problemTryLogService.deleteProblemTryLog(id, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }
}
