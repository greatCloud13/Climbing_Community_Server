package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.dto.GymSearchRequest;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GymRepositoryCustom {

    Page<Gym> search(GymSearchRequest request, Pageable pageable);
}
