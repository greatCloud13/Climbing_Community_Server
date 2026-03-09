package com.project.greatcloud13.ClimbingWith.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClearRecordCreateDTO {

    private Long userId;

    private Long settingId;

    private Long problemId;

    private String videoUrl;

    private LocalDate clearDate;
}
