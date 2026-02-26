package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Problem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemDTO {

    private Long id;

    private Long settingId;

    private String title;

    private String problemType;

    private String gymLevel;

    private Integer clearUserCount;

    private String description;

    private Float evaluation;

    public static ProblemDTO from(Problem problem){
        return ProblemDTO.builder()
                .id(problem.getId())
                .settingId(problem.getSetting().getId())
                .title(problem.getTitle())
                .problemType(problem.getProblemType().toString())
                .gymLevel(problem.getGymLevel().getLevelName())
                .clearUserCount(problem.getClearUserCount())
                .description(problem.getDescription())
                .evaluation(problem.getEvaluation())
                .build();
    }
}
