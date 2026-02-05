package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.common.SearchTag;
import com.project.greatcloud13.ClimbingWith.dto.UserDTO;
import com.project.greatcloud13.ClimbingWith.dto.UserDetailDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;

    @Transactional(readOnly = true)
    public Page<UserDTO> getUserList(int page, int size){

        Pageable pageable = PageRequest.of(page, size,
                Sort.by("id").descending());
        Page<User> userList = userRepository.findAll(pageable);

        return userList.map(UserDTO::from);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> searchUser(String keyword, SearchTag searchTag, int page){

        Pageable pageable = PageRequest.of(page, 100);

        Page<User> userList = switch (searchTag){
            case USERNAME -> userList = userRepository.findAllByUsername(keyword, pageable);
            case EMAIL -> userList = userRepository.findAllByEmail(keyword, pageable);
            case NICKNAME -> userList = userRepository.findAllByNickname(keyword, pageable);
        };

        return userList.map(UserDTO::from);
    }

    @Transactional(readOnly = true)
    public UserDetailDTO getUserDetail(String username){

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        return UserDetailDTO.from(user);
    }

    @Transactional
    public UserDetailDTO assignGymManager(Long gymId, String username){
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(()->new EntityNotFoundException("암장을 찾을 수 없습니다."));

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        user.assignGymManager(gym);

        return UserDetailDTO.from(user);
    }

    @Transactional
    public UserDetailDTO unassignGymManager(String username){

        User user = userRepository.findByUsername(username)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        user.unassignGym();

        return UserDetailDTO.from(user);
    }





}
