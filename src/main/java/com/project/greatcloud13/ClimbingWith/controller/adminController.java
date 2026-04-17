package com.project.greatcloud13.ClimbingWith.controller;

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
