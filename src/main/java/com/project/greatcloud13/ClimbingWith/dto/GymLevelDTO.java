package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GymLevelDTO {

    private Long id;
    private Long gymId;
    private String gymName;
    private String levelName;
    private Integer displayOrder;
    private String colorCode;
    private String description;

    public static GymLevelDTO from(GymLevel gymLevel) {
        return GymLevelDTO.builder()
                .id(gymLevel.getId())
                .gymId(gymLevel.getGym().getId())
                .gymName(gymLevel.getGym().getGymName())
                .levelName(gymLevel.getLevelName())
                .displayOrder(gymLevel.getDisplayOrder())
                .colorCode(gymLevel.getColorCode())
                .description(gymLevel.getDescription())
                .build();
    }
}
