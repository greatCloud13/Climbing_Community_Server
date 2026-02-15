package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.Sector;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SectorDetailDTO {

    private Long id;

    private String gymName;

    private String sectorName;

    private String settingDate;

    private String nextSettingDate;

    private List<Setting> settingList;

    public static SectorDetailDTO from(Sector sector, List<Setting> settingList){
        return SectorDetailDTO.builder()
                .id(sector.getId())
                .gymName(sector.getGym().getGymName())
                .settingDate(sector.getSettingDate() != null ? sector.getSettingDate().toString() : null)
                .nextSettingDate(sector.getNextSettingDate() != null ? sector.getNextSettingDate().toString() : null)
                .settingList(settingList)
                .build();

    }

}
