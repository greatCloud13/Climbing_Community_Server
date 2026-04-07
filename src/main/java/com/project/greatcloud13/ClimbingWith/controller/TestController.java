package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class TestController {

    private final NotificationService notificationService;

    @GetMapping("/sendNotification")
    public ResponseEntity<Boolean> sendNotification(@AuthenticationPrincipal CustomUserDetails userDetails){

        notificationService.sendNotification(userDetails.getUserId(), "알림 전송!");

        return ResponseEntity.ok(Boolean.TRUE);
    }

}
