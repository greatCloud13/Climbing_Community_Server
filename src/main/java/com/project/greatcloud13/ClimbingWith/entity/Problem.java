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

    private String gymLevelName;

    private String gymLevelColorCode;

    private Integer clearUserCount;

    private String description;

    private Float evaluation;

    private Integer holdCount;

    @Builder
    public Problem(Setting setting, Gym gym, String title, ProblemType problemType, GymLevel gymLevel, String description, Integer holdCount){
        this.setting = setting;
        this.gym = gym;
        this.title = title;
        this.problemType = problemType;
        this.gymLevel = gymLevel;
        this.clearUserCount = 0;
        this.evaluation = 3F;
        this.description = description;
        this.holdCount = holdCount;
    }

    public void update(String title, ProblemType problemType, GymLevel gymLevel, String description, Integer holdCount){
        this.title = title;
        this.problemType = problemType;
        this.gymLevel = gymLevel;
        this.description = description;
        this.holdCount = holdCount;
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

    /**
     * 암장 레벨이 삭제될 때 이 문제의 레벨 참조를 끊습니다.
     * 참조 무결성 때문에 gymLevel FK는 끊어지므로, 표시용으로 레벨명/색상을 스냅샷에 남겨둡니다.
     */
    public void detachGymLevel(){
        this.gymLevelName = gymLevel.getLevelName();
        this.gymLevelColorCode = gymLevel.getColorCode();
        this.gymLevel = null;
    }

}

