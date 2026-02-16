package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Setting;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SettingDTO {

    private Long id;

    private String sectorName;

    private String gymName;

    private String settingDate;

    private String startDate;

    private String endDate;

    private boolean isActive;

    public static SettingDTO from(Setting setting){
        return SettingDTO.builder()
                .id(setting.getId())
                .sectorName(setting.getSector().getSectorName())
                .gymName(setting.getGym().getGymName())
                .settingDate(setting.getSettingDate() != null ? setting.getSettingDate().toString() : null)
                .startDate(setting.getStartDate() != null ?setting.getSettingDate().toString() : null)
                .endDate(setting.getEndDate() != null ? setting.getEndDate().toString() : null)
                .isActive(setting.isActive())
                .build();
    }

}
