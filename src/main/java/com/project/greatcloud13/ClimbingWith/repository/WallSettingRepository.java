package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.Setting;
import org.springframework.data.jpa.repository.JpaRepository;

public interface WallSettingRepository extends JpaRepository<Setting, Long> {
}
