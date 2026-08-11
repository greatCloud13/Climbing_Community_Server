package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.ProblemTryLog;
import com.project.greatcloud13.ClimbingWith.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProblemTryLogRepository extends JpaRepository<ProblemTryLog, Long> {

    Page<ProblemTryLog> findAllByUser(User user, Pageable pageable);

    Page<ProblemTryLog> findAllByUserAndProblem(User user, Problem problem, Pageable pageable);

    long countByUserAndProblem(User user, Problem problem);

    Optional<ProblemTryLog> findTopByUserAndProblemOrderByDropPointDesc(User user, Problem problem);

    // 사용자별 해당 문제의 최고 도달 지점(dropPoint) 목록: 홀드별 낙하 분포 집계에 사용
    @Query("SELECT MAX(t.dropPoint) FROM ProblemTryLog t " +
            "WHERE t.problem = :problem AND t.dropPoint IS NOT NULL " +
            "GROUP BY t.user")
    List<Integer> findBestDropPointPerUser(@Param("problem") Problem problem);
}
