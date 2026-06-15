package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.common.SearchTag;
import com.project.greatcloud13.ClimbingWith.dto.UserDTO;
import com.project.greatcloud13.ClimbingWith.dto.UserDetailDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;

    public Page<UserDTO> getUserList(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return userRepository.findAll(pageable).map(UserDTO::from);
    }

    public Page<UserDTO> searchUser(String keyword, SearchTag searchTag, int page) {
        Pageable pageable = PageRequest.of(page, 100);

        Page<User> userList = switch (searchTag) {
            case USERNAME -> userRepository.findAllByUsername(keyword, pageable);
            case EMAIL -> userRepository.findAllByEmail(keyword, pageable);
            case NICKNAME -> userRepository.findAllByNickname(keyword, pageable);
        };

        return userList.map(UserDTO::from);
    }

    public UserDetailDTO getUserDetail(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        return UserDetailDTO.from(user);
    }

    @Transactional
    public UserDetailDTO assignGymManager(Long gymId, Long id) {
        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        user.assignGymManager(gym);

        return UserDetailDTO.from(user);
    }

    @Transactional
    public UserDetailDTO unassignGymManager(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(UserNotFoundException::new);

        user.unassignGym();

        return UserDetailDTO.from(user);
    }
}
