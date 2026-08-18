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

    private Long levelId;

    private String colorCode;

    private Integer holdCount;

    public static ProblemDTO from(Problem problem){
        boolean gymLevelDeleted = problem.getGymLevel() == null;

        return ProblemDTO.builder()
                .id(problem.getId())
                .settingId(problem.getSetting().getId())
                .title(problem.getTitle())
                .problemType(problem.getProblemType().toString())
                .levelId(gymLevelDeleted ? null : problem.getGymLevel().getId())
                .gymLevel(gymLevelDeleted ? problem.getGymLevelName() : problem.getGymLevel().getLevelName())
                .colorCode(gymLevelDeleted ? problem.getGymLevelColorCode() : problem.getGymLevel().getColorCode())
                .clearUserCount(problem.getClearUserCount())
                .description(problem.getDescription())
                .evaluation(problem.getEvaluation())
                .holdCount(problem.getHoldCount())
                .build();
    }
}
