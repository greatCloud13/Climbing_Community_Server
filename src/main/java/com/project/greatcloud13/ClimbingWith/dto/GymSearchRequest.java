package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Gym;
import lombok.Data;

@Data
public class GymSearchRequest {

    private String keyword;

    private Gym.GymType gymType;

    private String hashtag;

    private Boolean isActive = true;

    private int page = 0;

    private int size = 10;
}
