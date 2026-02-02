package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.Sector;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface WallSettingRepository extends JpaRepository<Setting, Long> {

    List<Setting> findAllBySector(Sector sector);

}
