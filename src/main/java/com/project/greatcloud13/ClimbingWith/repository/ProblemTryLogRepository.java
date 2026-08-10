package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.ProblemTryLog;
import com.project.greatcloud13.ClimbingWith.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProblemTryLogRepository extends JpaRepository<ProblemTryLog, Long> {

    Page<ProblemTryLog> findAllByUser(User user, Pageable pageable);

    long countByUserAndProblem(User user, Problem problem);

    Optional<ProblemTryLog> findTopByUserAndProblemOrderByDropPointDesc(User user, Problem problem);
}
