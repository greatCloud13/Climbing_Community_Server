package com.project.greatcloud13.ClimbingWith.controller;

import com.project.greatcloud13.ClimbingWith.common.SearchTag;
import com.project.greatcloud13.ClimbingWith.dto.UserDTO;
import com.project.greatcloud13.ClimbingWith.dto.UserDetailDTO;
import com.project.greatcloud13.ClimbingWith.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    public ResponseEntity<Page<UserDTO>> getAllUserPage(int size, int page){
        Page<UserDTO> result = userService.getUserList(page, size);

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<Page<UserDTO>> searchUser(String keyword, SearchTag tag, int page){
        Page<UserDTO> result = userService.searchUser(keyword, tag, page);

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<UserDetailDTO> getUserDetail(String username){
        UserDetailDTO result = userService.getUserDetail(username);

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<UserDetailDTO> assignGymManager(Long gymId, String username){
        UserDetailDTO result = userService.assignGymManager(gymId, username);

        return ResponseEntity.ok(result);
    }

    public ResponseEntity<UserDetailDTO> unassignGymManager(String username){
        UserDetailDTO result = userService.unassignGymManager(username);

        return ResponseEntity.ok(result);
    }

}
