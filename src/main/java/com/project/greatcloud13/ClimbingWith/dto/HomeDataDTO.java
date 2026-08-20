package com.project.greatcloud13.ClimbingWith.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HomeDataDTO {

    private List<GymCard> gymCardList;

    private List<Long> bookmarkIdList;

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GymCard {

        private Long gymId;

        private String gymName;

        private String address;

        private String imageUrl;

        private Long bookmarkId;

        private List<GymNotice> notices;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class GymNotice {

        private Long postId;

        private String noticeTitle;

        private String date;
    }
}
