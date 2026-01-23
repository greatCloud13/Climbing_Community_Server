package com.project.greatcloud13.ClimbingWith.dto;

import com.project.greatcloud13.ClimbingWith.entity.Gym;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GymCreateDTO {

    @NotBlank(message = "클라이밍장 이름은 필수업니다.")
    @Size(min = 2, max = 20, message = "클라이밍장 이름은 2~20자 이내여야합니다")
    private String gymName;

    private Gym.GymType type;

    @NotBlank(message = "클라이밍장 주소는 필수입니다.")
    @Size(min = 4, max = 20, message = "클라이밍장 주소를 정확하게 입력해주세요")
    private String address;

    private LocalTime openAt;

    private LocalTime closeAt;

    private LocalTime weekendOpenAt;

    private LocalTime weekendCloseAt;

    private String memo;
}
