package com.project.greatcloud13.ClimbingWith.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SectorUpdateDTO {

    private String sectorName;

    private LocalDate settingDate;

    private LocalDate nextSettingDate;

}
