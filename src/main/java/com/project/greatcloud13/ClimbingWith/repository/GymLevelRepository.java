package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import com.project.greatcloud13.ClimbingWith.entity.ProblemType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymLevelRepository extends JpaRepository<GymLevel, Long> {

    List<GymLevel> findAllByGym_Id(Long gymId);

    List<GymLevel> findAllByGym_IdAndClimbType(Long gymId, ProblemType climbType);

}
