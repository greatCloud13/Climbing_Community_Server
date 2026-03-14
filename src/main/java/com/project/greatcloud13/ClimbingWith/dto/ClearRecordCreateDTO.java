package com.project.greatcloud13.ClimbingWith.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class ClearRecordCreateDTO {

    private Long problemId;

    private String videoUrl;

    private LocalDate clearDate;
}
