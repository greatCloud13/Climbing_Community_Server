package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SettingDetailDTO {

    private Long id;

    private String sectorName;

    private String gymName;

    private String settingDate;

    private String startDate;

    private String endDate;

    private boolean isActive;

    private List<ProblemDTO> problemList;


    public static SettingDetailDTO from(Setting setting, List<ProblemDTO> problemList){
        return SettingDetailDTO.builder()
                .id(setting.getId())
                .sectorName(setting.getSector().getSectorName())
                .gymName(setting.getGym().getGymName())
                .settingDate(setting.getSettingDate() != null ? setting.getSettingDate().toString() : null)
                .startDate(setting.getStartDate() != null ? setting.getStartDate().toString() : null )
                .endDate(setting.getEndDate() != null ? setting.getEndDate().toString() : null)
                .isActive(setting.isActive())
                .problemList(problemList)
                .build();
    }
}
