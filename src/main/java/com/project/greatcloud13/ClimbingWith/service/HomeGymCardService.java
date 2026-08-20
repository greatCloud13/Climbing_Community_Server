package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.HomeDataDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.GymBookmark;
import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.repository.GymBookmarkRepository;
import com.project.greatcloud13.ClimbingWith.repository.PostRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeGymCardService {

    private static final int NOTICE_COUNT = 5;
    private static final DateTimeFormatter NOTICE_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy.MM.dd");

    private final GymBookmarkRepository gymBookmarkRepository;
    private final PostRepository postRepository;

    @Transactional(readOnly = true)
    public HomeDataDTO getHomeData(Long userId) {
        List<GymBookmark> bookmarks = gymBookmarkRepository.findAllByUser_IdOrderByIdDesc(userId);

        List<HomeDataDTO.GymCard> gymCardList = bookmarks.stream()
                .map(bookmark -> toGymCard(bookmark.getGym(), bookmark.getId()))
                .toList();

        List<Long> bookmarkIdList = bookmarks.stream()
                .map(GymBookmark::getId)
                .toList();

        return HomeDataDTO.builder().gymCardList(gymCardList).bookmarkIdList(bookmarkIdList).build();
    }

    private HomeDataDTO.GymCard toGymCard(Gym gym, Long bookmarkId) {
        List<HomeDataDTO.GymNotice> notices = postRepository
                .findAllByGym(gym, PageRequest.of(0, NOTICE_COUNT, Sort.by(Sort.Direction.DESC, "createdAt")))
                .stream()
                .map(this::toGymNotice)
                .toList();

        return HomeDataDTO.GymCard.builder()
                .gymId(gym.getId())
                .gymName(gym.getGymName())
                .address(gym.getAddress())
                .imageUrl(null) // #todo 차후 이미지 업로드 URL 기능 구현 후 추가 예정
                .notices(notices)
                .bookmarkId(bookmarkId)
                .build();
    }

    private HomeDataDTO.GymNotice toGymNotice(Post post) {
        return HomeDataDTO.GymNotice.builder()
                .postId(post.getId())
                .noticeTitle(post.getTitle())
                .date(post.getCreatedAt().format(NOTICE_DATE_FORMAT))
                .build();
    }
}
