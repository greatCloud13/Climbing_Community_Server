package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;

@Entity
@Getter
@Table(name = "gym")
public class Gym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private GymType gymType;

    private String address;

    private LocalTime openAt;

    private LocalTime closeAt;

    private LocalTime weekend_open_at;

    private LocalTime weekend_close_at;

    @Builder
    public Gym(GymType gymType, String address){
        this.gymType = gymType;
        this.address = address;
    }

    public enum GymType{
        BOULDER,
        LEAD
    }
}
