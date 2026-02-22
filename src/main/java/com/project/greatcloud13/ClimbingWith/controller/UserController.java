package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.common.SearchTag;
import com.project.greatcloud13.ClimbingWith.dto.UserDTO;
import com.project.greatcloud13.ClimbingWith.dto.UserDetailDTO;
import com.project.greatcloud13.ClimbingWith.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/size/{size}/page/{page}")
    public ResponseEntity<Page<UserDTO>> getAllUserPage(@PathVariable int size, @PathVariable int page){
        Page<UserDTO> result = userService.getUserList(page, size);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/keyword/{keyword}/searchTag/{tag}/page/{page}")
    public ResponseEntity<Page<UserDTO>> searchUser(@PathVariable String keyword, @PathVariable SearchTag tag, @PathVariable int page){
        Page<UserDTO> result = userService.searchUser(keyword, tag, page);

        return ResponseEntity.ok(result);
    }

    @GetMapping("/username/{username}")
    public ResponseEntity<UserDetailDTO> getUserDetail(@PathVariable String username){
        UserDetailDTO result = userService.getUserDetail(username);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/assign/{gymId}")
    public ResponseEntity<UserDetailDTO> assignGymManager(@PathVariable Long gymId, @RequestBody Long id){
        UserDetailDTO result = userService.assignGymManager(gymId, id);

        return ResponseEntity.ok(result);
    }

    @PutMapping("/unassign/{username}")
    public ResponseEntity<UserDetailDTO> unassignGymManager(@PathVariable String username){
        UserDetailDTO result = userService.unassignGymManager(username);

        return ResponseEntity.ok(result);
    }

}
