package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.ProblemType;
import lombok.Data;

@Data
public class ProblemUpdateDTO {

    private Long settingId;

    private String title;

    private ProblemType problemType;

    private Long gymLevelId;

    private String description;
}
