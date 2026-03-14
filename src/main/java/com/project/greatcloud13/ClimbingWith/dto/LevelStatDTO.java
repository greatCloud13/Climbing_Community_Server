package com.project.greatcloud13.ClimbingWith.dto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LevelStatDTO{
    private String levelName;
    private String colorCode;
    private Long count;

    public static LevelStatDTO from(LevelStat levelStat){
        return LevelStatDTO.builder()
                .levelName(levelStat.getLevel().getLevelName())
                .colorCode(levelStat.getLevel().getColorCode())
                .count(levelStat.getCount())
                .build();
    }
}