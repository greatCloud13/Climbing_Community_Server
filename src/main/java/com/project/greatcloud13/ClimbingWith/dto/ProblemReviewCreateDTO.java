package com.project.greatcloud13.ClimbingWith.dto;

import lombok.Data;

@Data
public class ProblemReviewCreateDTO {

    private Long problemId;

    private String problemHint;

    private Integer evaluation;
}
