package com.project.greatcloud13.ClimbingWith.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProblemDropPointStatsResponse {

    private Long problemId;

    private Integer holdCount;

    private Long totalUserCount;

    private List<DropPointStat> distribution;
}
