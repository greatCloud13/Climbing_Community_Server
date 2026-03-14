package com.project.greatcloud13.ClimbingWith.entity;

import com.project.greatcloud13.ClimbingWith.dto.ClearRecordCreateDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "clear_record")
public class ClearRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @ManyToOne
    @JoinColumn(name = "setting_id")
    private Setting setting;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private String videoUrl;

    private LocalDate clearDate;

    public void updateProblem(Problem problem){
        this.problem = problem;
    }
    public void updateVideoUrl(String url){
        this.videoUrl = url;
    }

    @Builder
    public ClearRecord(User user, Gym gym, Setting setting, Problem problem, String videoUrl){
        this.user = user;
        this.gym = gym;
        this.setting = setting;
        this.problem = problem;
        this.videoUrl = videoUrl;
        this.clearDate = LocalDate.now();
    }

}
