package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "sector")
public class Sector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @Column(name = "sector_name")
    private String sectorName;

    @Column(name = "setting_date")
    private LocalDate settingDate;

    @Column(name = "next_setting_date")
    private LocalDate nextSettingDate;

    @Builder
    public Sector(Gym gym, String sectorName, LocalDate settingDate, LocalDate nextSettingDate) {
        this.gym = gym;
        this.sectorName = sectorName;
        this.settingDate = settingDate;
        this.nextSettingDate = nextSettingDate;
    }

    public void update(String sectorName, LocalDate settingDate, LocalDate nextSettingDate){
        this.sectorName = sectorName;
        this.settingDate = settingDate;
        this.nextSettingDate = nextSettingDate;
    }
}
