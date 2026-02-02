package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Sector;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GymDetailDTO {

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

    private List<Sector> sectorList;

    public static GymDetailDTO from(Gym gym, List<Sector> sectorList){

        return GymDetailDTO.builder()
                .id(gym.getId())
                .gymType(gym.getGymType().toString())
                .gymName(gym.getGymName())
                .address(gym.getAddress())
                .openAt(gym.getGymName()==null ? null : gym.getOpenAt().toString())
                .closeAt(gym.getCloseAt()==null ? null : gym.getCloseAt().toString())
                .weekendOpenAt(gym.getWeekend_open_at()==null ? null : gym.getWeekend_open_at().toString())
                .weekendCloseAt(gym.getWeekend_close_at()==null ? null : gym.getWeekend_close_at().toString())
                .memo(gym.getMemo()==null ? null : gym.getMemo())
                .sectorList(sectorList)
                .isActive(gym.isActive())
                .build();
    }

}
