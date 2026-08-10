package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClearRecordRepository extends JpaRepository<ClearRecord, Long> {

    Page<ClearRecord> findAllByProblemAndVideoUrlIsNotNullAndIsClearTrueAndIsActiveTrue(Problem problem, Pageable pageable);

    Page<ClearRecord> findAllBySettingAndVideoUrlIsNotNullAndIsClearTrueAndIsActiveTrue(Setting setting, Pageable pageable);

    Page<ClearRecord> findAllByUserAndIsClearTrueAndIsActiveTrueOrderByClearDateDesc(User user, Pageable pageable);

    Page<ClearRecord> findAllByUserAndGymAndIsClearTrueAndIsActiveTrueOrderByClearDateDesc(User user, Gym gym, Pageable pageable);

    Page<ClearRecord> findAllByUserAndSettingAndIsClearTrueAndIsActiveTrueOrderByClearDateDesc(User user, Setting setting, Pageable pageable);

    List<ClearRecord> findAllByProblem(Problem problem);

    List<ClearRecord> findAllBySetting(Setting setting);

    long countByProblemAndIsClearTrueAndIsActiveTrue(Problem problem);
}
