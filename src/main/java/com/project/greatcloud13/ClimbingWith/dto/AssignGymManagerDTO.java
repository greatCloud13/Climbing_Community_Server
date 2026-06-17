package com.project.greatcloud13.ClimbingWith.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AssignGymManagerDTO {
    @NotNull
    private Long userId;
}
