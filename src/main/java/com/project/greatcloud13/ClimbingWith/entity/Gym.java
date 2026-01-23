package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gym")
public class Gym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gymName;

    @Enumerated(EnumType.STRING)
    private GymType gymType;

    private String address;

    private LocalTime openAt;

    private LocalTime closeAt;

    private LocalTime weekend_open_at;

    private LocalTime weekend_close_at;

    private LocalDateTime createdAt;

    private boolean isActive;

    private String memo;

    @Builder
    public Gym(String gymName, GymType gymType, String address, LocalTime openAt, LocalTime closeAt, LocalTime weekendOpenAt
    ,LocalTime weekendCloseAt, String memo){
        this.gymName = gymName;
        this.gymType = gymType;
        this.address = address;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.weekend_open_at = weekendOpenAt;
        this.weekend_close_at = weekendCloseAt;
        this.createdAt =LocalDateTime.now();
        this.isActive = true;
        this.memo = memo;

    }

    public enum GymType{
        BOULDER,
        LEAD,
        BOTH
    }
}
