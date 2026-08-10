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

    private String problemTitle;

    private String videoUrl;

    private LocalDate startDate;

    private LocalDate clearDate;

    private boolean isClear;

    private boolean isActive;

    public void updateProblem(Problem problem){
        this.problem = problem;
    }
    public void updateVideoUrl(String url){
        this.videoUrl = url;
    }

    /**
     * 문제/세팅이 삭제될 때 이 기록을 비활성화합니다.
     * 참조 무결성 때문에 problem/setting FK는 끊어지므로, 표시용으로 문제 제목을 스냅샷에 남겨둡니다.
     */
    public void deactivate(){
        this.problemTitle = problem.getTitle();
        this.problem = null;
        this.setting = null;
        this.isActive = false;
    }

    @Builder
    public ClearRecord(User user, Gym gym, Setting setting, Problem problem, String videoUrl, LocalDate clearDate){
        this.user = user;
        this.gym = gym;
        this.setting = setting;
        this.problem = problem;
        this.videoUrl = videoUrl;
        this.clearDate = clearDate;
        this.isActive = true;
    }

}
