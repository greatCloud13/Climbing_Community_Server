package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.ClearRecord;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClearRecordSummaryDTO {

    private String username;

    private String sectorName;

    private String settingPeriod;

    public static ClearRecordSummaryDTO from(ClearRecord clearRecord){
        return ClearRecordSummaryDTO.builder()
                .username(clearRecord.getUser().getUsername())
                .sectorName(clearRecord.getSetting().getSector().getSectorName())
                .settingPeriod(clearRecord.getSetting().getStartDate().toString() + " ~ " + clearRecord.getSetting().getEndDate().toString())
                .build();

    }
}
