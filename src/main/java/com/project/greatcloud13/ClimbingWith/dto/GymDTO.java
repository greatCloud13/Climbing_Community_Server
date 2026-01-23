package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Gym;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GymDTO {

    private Long id;

    private String gymName;

    private String gymType;

    private String address;

    private String openAt;

    private String closeAt;

    private String weekendOpenAt;

    private String weekendCloseAt;

    private Boolean isActive;

    private String memo;

    public static GymDTO from(Gym gym){

        return GymDTO.builder()
                .id(gym.getId())
                .gymType(gym.getGymType().toString())
                .gymName(gym.getGymName())
                .address(gym.getAddress())
                .openAt(gym.getGymName()==null ? null : gym.getOpenAt().toString())
                .closeAt(gym.getCloseAt()==null ? null : gym.getCloseAt().toString())
                .weekendOpenAt(gym.getWeekend_open_at()==null ? null : gym.getWeekend_open_at().toString())
                .weekendCloseAt(gym.getWeekend_close_at()==null ? null : gym.getWeekend_close_at().toString())
                .memo(gym.getMemo()==null ? null : gym.getMemo())
                .isActive(gym.isActive())
                .build();
    }

}
