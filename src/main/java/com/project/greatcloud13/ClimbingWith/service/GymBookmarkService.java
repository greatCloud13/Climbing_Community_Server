package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.BookmarkResultDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.GymBookmark;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymBookmarkRepository;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GymBookmarkService {

    private final UserRepository userRepository;
    private final GymRepository gymRepository;
    private final GymBookmarkRepository gymBookmarkRepository;

    public BookmarkResultDTO addBookmark(Long gymId, Long userId){
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

        GymBookmark bookmark = GymBookmark.builder().gym(gym).user(user).build();
        gymBookmarkRepository.save(bookmark);

        return BookmarkResultDTO.builder().id(bookmark.getId()).gymName(bookmark.getGym().getGymName()).build();
    }

    public void deleteBookmark(Long id){
        gymBookmarkRepository.deleteById(id);
    }
}
