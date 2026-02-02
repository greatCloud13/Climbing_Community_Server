package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymLevelRepository extends JpaRepository<GymLevel, Long> {

    List<GymLevel> findAllByGym_Id(Long gymId);

}
