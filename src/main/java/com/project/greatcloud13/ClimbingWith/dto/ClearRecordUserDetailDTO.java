package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.ClearRecord;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ClearRecordUserDetailDTO {

    private Long clearRecordId;

    private String username;

    private Long problemId;

    private String problemName;

    private String level;

    private String levelColorCode;

    private Long gymId;

    private String gymName;

    private Long sectorId;

    private String sectorName;

    private Long settingId;

    private String clearDate;

    private long tryCount;

    public static ClearRecordUserDetailDTO from(ClearRecord clearRecord, long tryCount){
        return ClearRecordUserDetailDTO.builder()
                .clearRecordId(clearRecord.getId())
                .username(clearRecord.getUser().getUsername())
                .problemId(clearRecord.getProblem().getId())
                .problemName(clearRecord.getProblem().getTitle())
                .level(clearRecord.getProblem().getGymLevel().getLevelName())
                .levelColorCode(clearRecord.getProblem().getGymLevel().getColorCode())
                .gymId(clearRecord.getGym().getId())
                .gymName(clearRecord.getGym().getGymName())
                .sectorId(clearRecord.getSetting().getSector().getId())
                .sectorName(clearRecord.getSetting().getSector().getSectorName())
                .settingId(clearRecord.getSetting().getId())
                .clearDate(clearRecord.getClearDate().toString())
                .tryCount(tryCount)
                .build();
    }
}
