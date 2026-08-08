package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.HomeDataDTO;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.HomeGymCardService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/home")
@RequiredArgsConstructor
public class HomeController {

    private final HomeGymCardService homeGymCardService;

    @Operation(
            summary = "홈 화면 암장 카드 조회",
            description = "로그인한 사용자가 북마크한 암장 카드와 암장별 최근 공지 5개를 조회합니다."
    )
    @GetMapping
    public ResponseEntity<HomeDataDTO> getHomeData(@AuthenticationPrincipal CustomUserDetails userDetails) {
        HomeDataDTO result = homeGymCardService.getHomeData(userDetails.getUserId());

        return ResponseEntity.ok(result);
    }
}
