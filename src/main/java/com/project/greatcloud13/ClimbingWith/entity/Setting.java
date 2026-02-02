package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "setting")
public class Setting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sector_id")
    private Sector sector;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    private LocalDate settingDate;

    private LocalDate startDate;

    private LocalDate endDate;

    private boolean isActive;

    @Builder
    public Setting(Sector sector, Gym gym, LocalDate settingDate, LocalDate startDate, LocalDate endDate){
        this.sector = sector;
        this.gym = gym;
        this.settingDate = settingDate;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isActive = true;
    }

    public void update(LocalDate settingDate, LocalDate startDate, LocalDate endDate){
        this.settingDate = settingDate;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
