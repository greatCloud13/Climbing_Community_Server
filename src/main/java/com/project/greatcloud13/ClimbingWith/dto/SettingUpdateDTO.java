package com.project.greatcloud13.ClimbingWith.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SettingUpdateDTO {

    private LocalDate settingDate;

    private LocalDate startDate;

    private LocalDate endDate;

}
