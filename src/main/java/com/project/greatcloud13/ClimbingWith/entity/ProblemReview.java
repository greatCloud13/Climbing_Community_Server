package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "problem_review")
public class ProblemReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name ="problem_id")
    private Problem problem;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String problemHint;

    private Integer evaluation;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @Builder
    public ProblemReview(Problem problem, User user, String problemHint, Integer evaluation){
        this.problem = problem;
        this.user = user;
        this.problemHint = problemHint;
        this.evaluation = evaluation;
        this.createdAt = LocalDateTime.now();
    }

    public void update(String problemHint, Integer evaluation){
        this.problemHint = problemHint;
        this.evaluation = evaluation;
        this.updatedAt = LocalDateTime.now();
    }

}
