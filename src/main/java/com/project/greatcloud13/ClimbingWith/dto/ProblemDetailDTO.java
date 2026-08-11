package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Problem;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ProblemDetailDTO {

    private Long id;

    private Long settingId;

    private String title;

    private String problemType;

    private String gymLevel;

    private Long levelId;

    private String colorCode;

    private Integer holdCount;

    private String description;

    private Float evaluation;

    private Long myTryCount;

    private Long clearCount;

    private Integer myBestDropPoint;

    public static ProblemDetailDTO from(Problem problem, long myTryCount, long clearCount, Integer myBestDropPoint){
        return ProblemDetailDTO.builder()
                .id(problem.getId())
                .settingId(problem.getSetting().getId())
                .title(problem.getTitle())
                .problemType(problem.getProblemType().toString())
                .levelId(problem.getGymLevel().getId())
                .gymLevel(problem.getGymLevel().getLevelName())
                .colorCode(problem.getGymLevel().getColorCode())
                .holdCount(problem.getHoldCount())
                .description(problem.getDescription())
                .evaluation(problem.getEvaluation())
                .myTryCount(myTryCount)
                .clearCount(clearCount)
                .myBestDropPoint(myBestDropPoint)
                .build();
    }
}
