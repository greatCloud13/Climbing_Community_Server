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

    private String settingDate;

    private String nextSettingDate;

    public static SectorDTO from(Sector sector){
        return SectorDTO.builder()
                .id(sector.getId())
                .gymId(sector.getGym().getId())
                .sectorName(sector.getSectorName())
                .settingDate(sector.getSettingDate() != null ? sector.getSettingDate().toString() : null )
                .nextSettingDate(sector.getNextSettingDate() != null ? sector.getNextSettingDate().toString() : null)
                .build();
    }

}
