package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewUpdateDTO;
import com.project.greatcloud13.ClimbingWith.service.ProblemReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/review")
@RequiredArgsConstructor
public class ProblemReviewController {

    private final ProblemReviewService problemReviewService;

    @PostMapping
    public ResponseEntity<ProblemReviewDTO> createProblemReview(@RequestBody ProblemReviewCreateDTO request){

        ProblemReviewDTO result = problemReviewService.createReview(request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemReviewDTO> getProblemReview(@PathVariable Long id){

        ProblemReviewDTO result = problemReviewService.getReview(id);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/problem/{problemId}/page/{page}")
    public ResponseEntity<Page<ProblemReviewDTO>> getProblemReviewByProblemId(@PathVariable Long problemId, @PathVariable int page){
        Page<ProblemReviewDTO> result = problemReviewService.getReviewByProblemId(page, problemId);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemReviewDTO> updateProblemReview(@PathVariable Long id, @RequestBody ProblemReviewUpdateDTO request){
        ProblemReviewDTO result = problemReviewService.updateReview(id, request);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteProblemReview(@PathVariable Long id){
        problemReviewService.deleteReview(id);

        return ResponseEntity.ok(true);
    }
}
