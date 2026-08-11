package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "problem_try_log")
public class ProblemTryLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "problem_id")
    private Problem problem;

    private LocalDate tryDate;

    private String memo;

    private Integer dropPoint;

    public void updateMemo(String memo){
        this.memo = memo;
    }

    public void updateDropPoint(Integer dropPoint){
        this.dropPoint = dropPoint;
    }

    @Builder
    public ProblemTryLog(User user, Problem problem, String memo, Integer dropPoint){
        this.user = user;
        this.problem = problem;
        this.tryDate = LocalDate.now();
        this.memo = memo;
        this.dropPoint = dropPoint;
    }
}
