package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.ProblemType;
import lombok.Data;

@Data
public class GymLevelCreateDTO {

    private Long gymId;

    private String levelName;

    private Integer displayOrder;

    private String colorCode;

    private String description;

    private ProblemType climbType;
}
