package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
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

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    private String title;

    @Enumerated(EnumType.STRING)
    private ProblemType problemType;

    @ManyToOne
    @JoinColumn(name = "gym_level")
    private GymLevel gymLevel;

    private Integer clearUserCount;

    private String description;

    private Float evaluation;

    @Builder
    public Problem(Setting setting, Gym gym, String title, ProblemType problemType, GymLevel gymLevel, String description){
        this.setting = setting;
        this.gym = gym;
        this.title = title;
        this.problemType = problemType;
        this.gymLevel = gymLevel;
        this.clearUserCount = 0;
        this.evaluation = 3F;
        this.description = description;
    }

    public void update(String title, ProblemType problemType, GymLevel gymLevel, String description){
        this.title = title;
        this.problemType = problemType;
        this.gymLevel = gymLevel;
        this.description = description;
    }

    public void addClearUserCount(){
        clearUserCount++;
    }

    public void subClearUserCount(){
        clearUserCount--;
    }

    public void updateEvaluation(Float evaluation){
        this.evaluation = evaluation;
    }

}

