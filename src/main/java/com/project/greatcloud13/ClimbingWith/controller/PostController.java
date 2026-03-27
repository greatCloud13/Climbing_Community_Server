package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.dto.PostCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostResponseDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostSummaryDTO;
import com.project.greatcloud13.ClimbingWith.dto.PostUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import com.project.greatcloud13.ClimbingWith.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/post")
public class PostController {

    private final PostService postService;

    @Operation(
            summary = "신규 게시글 작성",
            description = "새로운 게시글을 작성합니다."

    )
    @PostMapping
    public ResponseEntity<PostResponseDTO> createPost(@RequestBody PostCreateDTO request, @AuthenticationPrincipal CustomUserDetails userDetails){
        PostResponseDTO result = postService.createPost(userDetails.getUserId(), request);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "기존 게시글 업데이트",
            description = "요청한 ID의 게시글을 수정합니다."

    )
    @PutMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> updatePost(@RequestBody PostUpdateDTO request, @PathVariable Long postId, @AuthenticationPrincipal CustomUserDetails userDetails){
        PostResponseDTO result = postService.updatePost(userDetails.getUserId(), postId, request);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary ="게시글 상세 조회",
            description = "등록된 게시글의 상세 내용을 조회합니다"

    )
    @GetMapping("/{postId}")
    public ResponseEntity<PostResponseDTO> getPost(@PathVariable Long postId){
        PostResponseDTO result = postService.getPostById(postId);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "암장 게시글 페이지 조회",
            description = "암장의 게시글 페이지를 조회합니다. 페이지네이션이 적용되어있습니다."
    )
    @GetMapping("/gym/{gymId}")
    public ResponseEntity<Page<PostSummaryDTO>> getAllByGym(@PathVariable Long gymId, Pageable pageable){
        Page<PostSummaryDTO> result = postService.getAllByGym(gymId, pageable);

        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "게시글 타입 기준 암장 게시글 페이지 조회",
            description = "지정한 암장의 요청한 게시글 타입과 일치하는 게시글 페이지를 조회합니다. "
    )
    @GetMapping("/gym/{gymId}/posttype/{postType}")
    public ResponseEntity<Page<PostSummaryDTO>> getAllByGymWIthPostType(@PathVariable Long gymId, @PathVariable PostType postType, Pageable pageable){
        Page<PostSummaryDTO> result = postService.getAllByGymWithPostType(gymId, postType, pageable);

        return ResponseEntity.ok(result);
    }
}
