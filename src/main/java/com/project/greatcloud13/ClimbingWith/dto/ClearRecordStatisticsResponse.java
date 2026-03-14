package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Gym;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClearRecordStatisticsResponse {

    private String gymName;
    private List<LevelStatDTO> levelStatDTOList;
    private Long totalCount;

    public static ClearRecordStatisticsResponse from(ClearRecordStatistics statistics){
        ClearRecordStatisticsResponse result = new ClearRecordStatisticsResponse();
        result.gymName = statistics.getGym().getGymName();
        result.levelStatDTOList = statistics.getProblemCountList().stream().map(LevelStatDTO :: from).toList();
        result.totalCount = statistics.getTotalClearCount();
        return result;
    }

}
