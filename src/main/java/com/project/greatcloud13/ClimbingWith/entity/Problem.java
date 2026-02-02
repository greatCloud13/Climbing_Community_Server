package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "problem")
public class Problem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "setting_id")
    private Setting setting;

    private String title;

    @Enumerated(EnumType.STRING)
    private ProblemType problemType;

    @ManyToOne
    @JoinColumn(name = "gym_level")
    private GymLevel gymLevel;

    private Integer clearUserCount;

    private String description;

    enum problemType{
        BOULDER,
        LEAD;
    }
}

