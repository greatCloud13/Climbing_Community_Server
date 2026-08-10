package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.ProblemTryLog;
import com.project.greatcloud13.ClimbingWith.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProblemTryLogRepository extends JpaRepository<ProblemTryLog, Long> {

    Page<ProblemTryLog> findAllByUser(User user, Pageable pageable);
}
