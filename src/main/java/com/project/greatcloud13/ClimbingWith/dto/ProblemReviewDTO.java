package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.ProblemReview;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemReviewDTO {
    private Long id;

    private Long problemId;

    private Long userId;

    private String problemHint;

    private Integer evaluation;

    private String createdAt;

    private String updatedAt;

    public static ProblemReviewDTO from(ProblemReview problemReview){
        return ProblemReviewDTO.builder()
                .id(problemReview.getId())
                .problemId(problemReview.getProblem().getId())
                .userId(problemReview.getUser().getId())
                .problemHint(problemReview.getProblemHint())
                .evaluation(problemReview.getEvaluation())
                .createdAt(problemReview.getCreatedAt().toString())
                .updatedAt(problemReview.getUpdatedAt() != null ? problemReview.getUpdatedAt().toString() : null)
                .build();

    }
}
