package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Sector;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SectorDTO {

    private Long id;

    private Long gymId;

    private String sectorName;

    private String description;

    private String settingDate;

    private String nextSettingDate;

    private long problemCount;

    public static SectorDTO from(Sector sector, long problemCount){
        return SectorDTO.builder()
                .id(sector.getId())
                .gymId(sector.getGym().getId())
                .sectorName(sector.getSectorName())
                .description(sector.getDescription())
                .settingDate(sector.getSettingDate() != null ? sector.getSettingDate().toString() : null )
                .nextSettingDate(sector.getNextSettingDate() != null ? sector.getNextSettingDate().toString() : null)
                .problemCount(problemCount)
                .build();
    }

}
