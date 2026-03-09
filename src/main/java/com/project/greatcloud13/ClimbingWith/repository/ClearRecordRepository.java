package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.ClearRecord;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import com.project.greatcloud13.ClimbingWith.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClearRecordRepository extends JpaRepository<ClearRecord, Long> {

    Page<ClearRecord> findAllByProblemAndVideoUrlIsNotNull(Problem problem, Pageable pageable);

    Page<ClearRecord> findAllBySettingAndVideoUrlIsNotNull(Setting setting, Pageable pageable);

    Page<ClearRecord> findAllByUserOrderByClearDateDesc(User user, Pageable pageable);
}
