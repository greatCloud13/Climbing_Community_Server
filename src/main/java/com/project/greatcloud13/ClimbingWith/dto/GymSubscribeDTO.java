package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.GymSubscribe;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GymSubscribeDTO {

    private Long id;

    private Long userId;

    private String username;

    private Long gymId;

    private String gymName;

    private Boolean isActive;

    private String subscribeAt;

    public static GymSubscribeDTO from(GymSubscribe gymSubscribe){
        return GymSubscribeDTO.builder()
                .id(gymSubscribe.getId())
                .userId(gymSubscribe.getUser().getId())
                .username(gymSubscribe.getUser().getUsername())
                .gymId(gymSubscribe.getId())
                .gymName(gymSubscribe.getGym().getGymName())
                .isActive(gymSubscribe.getIsActive())
                .subscribeAt(gymSubscribe.getSubscribeAt().toString())
                .build();
    }

}
