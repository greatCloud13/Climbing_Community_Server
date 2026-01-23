package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.GymCreateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
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
public class GymManagementService {

    private final GymRepository gymRepository;

    @Transactional
    public Gym createGym(GymCreateDTO request){

        Gym gym = Gym.builder()
                .gymName(request.getGymName())
                .gymType(request.getType())
                .address(request.getAddress())
                .openAt(request.getOpenAt())
                .closeAt(request.getCloseAt())
                .weekendOpenAt(request.getWeekendOpenAt())
                .weekendCloseAt(request.getWeekendCloseAt())
                .memo(request.getMemo())
                .build();

        return gymRepository.save(gym);
    }

    @Transactional(readOnly = true)
    public Page<Gym> findAll(int page, int size){
        Pageable pageable = PageRequest.of(page, size,
                Sort.by("createdAt").descending());
        return gymRepository.findAll(pageable);
    }

}
