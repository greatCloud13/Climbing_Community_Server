package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.ProblemCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemUpdateDTO;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<ProblemDTO> createProblem(@RequestBody ProblemCreateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProblemDTO result = problemService.createProblem(request, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @GetMapping("/setting/{id}")
    public ResponseEntity<List<ProblemDTO>> getProblemListBySettingId(@PathVariable Long id) {
        List<ProblemDTO> result = problemService.getProblemListBySetting(id);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/gymLevel/{id}")
    public ResponseEntity<List<ProblemDTO>> getProblemListByGymLevelId(@PathVariable Long id) {
        List<ProblemDTO> result = problemService.getProblemByGymLevel(id);

        return ResponseEntity.ok(result);
    }

    @GetMapping("gym/{id}/page/{page}")
    public ResponseEntity<Page<ProblemDTO>> getProblemListByGymId(@PathVariable Long id, @PathVariable int page) {
        Page<ProblemDTO> result = problemService.getProblemPageByGym(id, page);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDTO> getProblemById(@PathVariable Long id) {
        ProblemDTO result = problemService.getProblem(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemDTO> updateProblem(@PathVariable Long id, @RequestBody ProblemUpdateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails) {
        ProblemDTO result = problemService.updateProblem(id, request, userDetails.getUserId());

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProblem(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        problemService.deleteProblem(id, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }
}
