package com.project.greatcloud13.ClimbingWith.entity;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@Table(
        name = "gym_subscribe",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_user_gym",
                        columnNames = {"user_id", "gym_id"}
                )
        }

)
public class GymSubscribe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "gym_id")
    private Gym gym;

    private Boolean isActive;

    private LocalDateTime subscribeAt;

    private LocalDateTime unSubscribeAt;

    @Builder
    public GymSubscribe(User user, Gym gym, LocalDateTime subscribeAt){
        this.user = user;
        this.gym = gym;
        this.isActive = true;
        this.subscribeAt = subscribeAt;
    }

    public void unSubscribe(LocalDateTime unSubscribeAt){
        this.unSubscribeAt = unSubscribeAt;
        this.isActive = false;
    }

    public void setSubscribe(LocalDateTime subscribeAt){
        this.isActive = true;
        this.subscribeAt=subscribeAt;
    }

}
