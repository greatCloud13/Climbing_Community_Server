package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.GymCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymDetailDTO;
import com.project.greatcloud13.ClimbingWith.dto.GymSearchRequest;
import com.project.greatcloud13.ClimbingWith.dto.GymUpdateDTO;
import com.project.greatcloud13.ClimbingWith.dto.SectorDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.common.AccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymImageLimitExceededException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.SectorRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GymManagementService {

    private static final int MAX_INTRO_IMAGE_COUNT = 10;

    private final GymRepository gymRepository;
    private final SectorRepository sectorRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    @Transactional
    public GymDTO createGym(GymCreateDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isAdmin()) {
            throw new AccessDeniedException();
        }

        validateIntroImageCount(request.getIntroImages());

        Gym gym = Gym.builder()
                .gymName(request.getGymName())
                .gymType(request.getType())
                .address(request.getAddress())
                .openAt(request.getOpenAt())
                .closeAt(request.getCloseAt())
                .weekendOpenAt(request.getWeekendOpenAt())
                .weekendCloseAt(request.getWeekendCloseAt())
                .memo(request.getMemo())
                .hashtags(request.getHashtags())
                .introImages(request.getIntroImages())
                .build();

        return GymDTO.from(gymRepository.save(gym));
    }

    private void validateIntroImageCount(List<String> introImages) {
        if (introImages != null && introImages.size() > MAX_INTRO_IMAGE_COUNT) {
            throw new GymImageLimitExceededException();
        }
    }

    public Page<GymDTO> findAll(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return gymRepository.findAll(pageable).map(GymDTO::from);
    }

    public Page<GymDTO> search(GymSearchRequest request) {
        Pageable pageable = PageRequest.of(request.getPage(), request.getSize());
        return gymRepository.search(request, pageable).map(GymDTO::from);
    }

    @Transactional
    public GymDTO updateGym(Long id, GymUpdateDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        if (!user.isAdmin()) {
            throw new AccessDeniedException();
        }

        validateIntroImageCount(request.getIntroImages());

        Gym gym = gymRepository.findById(id)
                .orElseThrow(GymNotFoundException::new);

        gym.updateGym(request);

        return GymDTO.from(gym);
    }

    public GymDetailDTO getGymDetail(Long id) {
        Gym gym = gymRepository.findById(id)
                .orElseThrow(GymNotFoundException::new);

        List<SectorDTO> sectorList = sectorRepository.findAllByGym(gym).stream()
                .map(sector -> SectorDTO.from(sector, problemRepository.countBySetting_Sector(sector)))
                .toList();

        return GymDetailDTO.from(gym, sectorList);
    }
}
