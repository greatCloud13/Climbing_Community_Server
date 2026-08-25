package com.project.greatcloud13.ClimbingWith.controller;

// EC2 배포 환경에 GPU가 없어 임베딩 기능을 임시 비활성화함에 따라,
// 임베딩 마이그레이션을 트리거하는 이 컨트롤러도 함께 비활성화.
// 재활성화 시 아래 주석을 해제하면 됨.
/*
import com.project.greatcloud13.ClimbingWith.batch.DataMigrationService;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/admin")
@RequiredArgsConstructor
public class adminController {

    private final DataMigrationService migrationService;

    @PostMapping("/migrate")
    public ResponseEntity<String> migrate(@AuthenticationPrincipal CustomUserDetails userDetails) {

        CompletableFuture.runAsync(()->migrationService.migrationExistingPosts(userDetails.getUserId()));
        return ResponseEntity.accepted().body("게시판 마이그레이션이 백그라운드에서 시작되었습니다.");
    }

}
*/
