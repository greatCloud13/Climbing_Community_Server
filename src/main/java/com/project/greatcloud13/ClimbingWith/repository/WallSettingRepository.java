package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Sector;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WallSettingRepository extends JpaRepository<Setting, Long> {

    List<Setting> findAllBySector(Sector sector);

    Optional<Setting> findFirstByGymOrderByIdDesc(Gym gym);

    List<Setting> findTop2ByGymOrderByIdDesc(Gym gym);

    List<Setting> findAllByGymAndIsActive(Gym gym,boolean isActive);

    Optional<Setting> findTopBySectorAndIsActiveOrderBySettingDateDesc(Sector sector, boolean isActive);
}
