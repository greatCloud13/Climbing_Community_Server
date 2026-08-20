package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.BookmarkResultDTO;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.GymBookmarkService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookmark")
@RequiredArgsConstructor
public class GymBookmarkController {

    private final GymBookmarkService gymBookmarkService;

    @PostMapping("/{gymId}")
    public ResponseEntity<BookmarkResultDTO> addBookmark(@PathVariable Long gymId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        BookmarkResultDTO result = gymBookmarkService.addBookmark(gymId, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @DeleteMapping("/{gymId}")
    public ResponseEntity<Void> deleteBookmark(@PathVariable Long gymId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        gymBookmarkService.deleteBookmark(gymId);

        return ResponseEntity.noContent().build();
    }
}
