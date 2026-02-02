package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "gym_level")
public class GymLevel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    private String levelName;

    private Integer displayOrder;

    private String colorCode;

    private String description;

    @Builder
    public GymLevel(Gym gym, String levelName, Integer displayOrder, String colorCode, String description){
        this.gym = gym;
        this.levelName = levelName;
        this.displayOrder = displayOrder;
        this.colorCode = colorCode;
        this.description = description;
    }

    public void updateGymLevel(String levelName, Integer displayOrder, String colorCode, String description){
        this.levelName = levelName;
        this.displayOrder = displayOrder;
        this.colorCode = colorCode;
        this.description = description;
    }

}
