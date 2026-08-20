package com.project.greatcloud13.ClimbingWith.entity;

import com.project.greatcloud13.ClimbingWith.dto.GymCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymUpdateDTO;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gym")
public class Gym {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String gymName;

    @Enumerated(EnumType.STRING)
    private GymType gymType;

    private String address;

    private LocalTime openAt;

    private LocalTime closeAt;

    private LocalTime weekend_open_at;

    private LocalTime weekend_close_at;

    private LocalDateTime createdAt;

    private boolean isActive;

    private String memo;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "gym_hashtag", joinColumns = @JoinColumn(name = "gym_id"))
    @Column(name = "hashtag")
    private List<String> hashtags = new ArrayList<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(name = "gym_intro_image", joinColumns = @JoinColumn(name = "gym_id"))
    @OrderColumn(name = "image_order")
    @Column(name = "image_url")
    private List<String> introImages = new ArrayList<>();

    @Builder
    public Gym(String gymName, GymType gymType, String address, LocalTime openAt, LocalTime closeAt, LocalTime weekendOpenAt
    ,LocalTime weekendCloseAt, String memo, List<String> hashtags, List<String> introImages){
        this.gymName = gymName;
        this.gymType = gymType;
        this.address = address;
        this.openAt = openAt;
        this.closeAt = closeAt;
        this.weekend_open_at = weekendOpenAt;
        this.weekend_close_at = weekendCloseAt;
        this.createdAt =LocalDateTime.now();
        this.isActive = true;
        this.memo = memo;
        this.hashtags = hashtags != null ? hashtags : new ArrayList<>();
        this.introImages = introImages != null ? introImages : new ArrayList<>();

    }

    public void updateGym(GymCreateDTO updateDTO){
        this.gymName =updateDTO.getGymName();
        this.gymType = updateDTO.getType();
        this.address = updateDTO.getAddress();
        this.openAt = updateDTO.getOpenAt();
        this.closeAt = updateDTO.getCloseAt();
        this.weekend_open_at = updateDTO.getWeekendOpenAt();
        this.weekend_close_at = updateDTO.getWeekendCloseAt();
        this.memo = updateDTO.getMemo();
        this.hashtags = updateDTO.getHashtags() != null ? updateDTO.getHashtags() : new ArrayList<>();
        this.introImages = updateDTO.getIntroImages() != null ? updateDTO.getIntroImages() : new ArrayList<>();
    }

    public void updateGym(GymUpdateDTO updateDTO){
        this.gymName = updateDTO.getGymName();
        this.address = updateDTO.getAddress();
        this.openAt = updateDTO.getOpenAt();
        this.closeAt = updateDTO.getCloseAt();
        this.weekend_open_at = updateDTO.getWeekendOpenAt();
        this.weekend_close_at = updateDTO.getWeekendCloseAt();
        this.memo = updateDTO.getMemo();
        this.hashtags = updateDTO.getHashtags() != null ? updateDTO.getHashtags() : new ArrayList<>();
        this.introImages = updateDTO.getIntroImages() != null ? updateDTO.getIntroImages() : new ArrayList<>();
    }

    public enum GymType{
        BOULDER,
        LEAD,
        BOTH
    }
}
