package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.ProblemTryLog;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProblemTryLogDTO {

    private Long id;

    private Long userId;

    private Long problemId;

    private String tryDate;

    private String memo;

    private Integer dropPoint;

    @Builder
    public ProblemTryLogDTO(ProblemTryLog log){
        this.id = log.getId();
        this.userId = log.getUser().getId();
        this.problemId = log.getProblem().getId();
        this.tryDate = log.getTryDate().toString();
        this.memo = log.getMemo();
        this.dropPoint = log.getDropPoint();
    }
}
