package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.ProblemCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemDTO;
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
    public ResponseEntity<ProblemDTO> createProblem(ProblemCreateDTO request){
        ProblemDTO result = problemService.createProblem(request);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/setting/{id}")
    public ResponseEntity<List<ProblemDTO>> getProblemListBySettingId(@PathVariable Long id){
        List<ProblemDTO> result = problemService.getProblemListBySetting(id);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProblemDTO> getProblemById(@PathVariable Long id){
        ProblemDTO result = problemService.getProblem(id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProblemDTO> updateProblem(@PathVariable Long id, @RequestBody ProblemUpdateDTO request){
        ProblemDTO result =problemService.updateProblem(id, request);

        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Boolean> deleteProblem(@PathVariable Long id){
        problemService.deleteProblem(id);

        return ResponseEntity.ok(true);
    }

}
