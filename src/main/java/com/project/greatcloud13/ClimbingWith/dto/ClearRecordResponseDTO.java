package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.ClearRecord;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import com.project.greatcloud13.ClimbingWith.entity.User;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class ClearRecordResponseDTO {

    private Long id;

    private String username;

    private Long settingId;

    private Long problemId;

    private String videoUrl;

    private String clearDate;

    public static ClearRecordResponseDTO from(ClearRecord clearRecord){
        return ClearRecordResponseDTO
                .builder()
                .id(clearRecord.getId())
                .username(clearRecord.getUser().getUsername())
                .settingId(clearRecord.getSetting().getId())
                .problemId(clearRecord.getProblem().getId())
                .videoUrl(clearRecord.getVideoUrl())
                .clearDate(clearRecord.getClearDate().toString())
                .build();
    }

}
