package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "gym_book_mark",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_user_gym",
                        columnNames = {"user_id", "gym_id"}
                )
        }
)
public class GymBookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    @Builder
    public GymBookmark(Gym gym, User user){
        this.gym = gym;
        this.user =user;
    }
}
