package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClearRecordRepository extends JpaRepository<ClearRecord, Long> {

    Page<ClearRecord> findAllByProblemAndVideoUrlIsNotNullAndIsActiveTrue(Problem problem, Pageable pageable);

    Page<ClearRecord> findAllBySettingAndVideoUrlIsNotNullAndIsActiveTrue(Setting setting, Pageable pageable);

    Page<ClearRecord> findAllByUserAndIsActiveTrueOrderByClearDateDesc(User user, Pageable pageable);

    Page<ClearRecord> findAllByUserAndGymAndIsActiveTrueOrderByClearDateDesc(User user, Gym gym, Pageable pageable);

    Page<ClearRecord> findAllByUserAndSettingAndIsActiveTrueOrderByClearDateDesc(User user, Setting setting, Pageable pageable);

    List<ClearRecord> findAllByProblem(Problem problem);

    List<ClearRecord> findAllBySetting(Setting setting);
}
