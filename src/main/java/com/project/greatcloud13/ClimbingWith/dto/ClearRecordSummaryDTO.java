package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.ClearRecord;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClearRecordSummaryDTO {

    private Long clearRecordId;

    private String username;

    private String problemName;

    private String level;

    private String gymName;

    private String sectorName;

    private String clearDate;

    public static ClearRecordSummaryDTO from(ClearRecord clearRecord){
        return ClearRecordSummaryDTO.builder()
                .clearRecordId(clearRecord.getId())
                .username(clearRecord.getUser().getUsername())
                .problemName(clearRecord.getProblem().getTitle())
                .level(clearRecord.getProblem().getGymLevel().getLevelName())
                .gymName(clearRecord.getGym().getGymName())
                .sectorName(clearRecord.getSetting().getSector().getSectorName())
                .clearDate(clearRecord.getClearDate().toString())
                .build();

    }
}
