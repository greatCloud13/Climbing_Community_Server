package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.ProblemCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.service.ProblemService;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/problem")
@RequiredArgsConstructor
public class ProblemController {

    private final ProblemService problemService;

    @PostMapping
    public ResponseEntity<Problem> createProblem(ProblemCreateDTO request){
        Problem result = problemService.createProblem(request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/setting/{id}")
    public ResponseEntity<List<Problem>> getProblemListBySettingId(@PathVariable Long id){
        List<Problem> result = problemService.getProblemListBySetting(id);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Problem> getProblemById(@PathVariable Long id){
        Problem result = problemService.getProblem(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Problem> updateProblem(@PathVariable Long id, @RequestBody ProblemUpdateDTO request){
        Problem result =problemService.updateProblem(id, request);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteProblem(@PathVariable Long id){
        problemService.deleteProblem(id);

        return ResponseEntity.ok(true);
    }

}
