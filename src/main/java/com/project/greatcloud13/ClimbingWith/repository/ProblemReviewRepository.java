package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.ProblemReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface ProblemReviewRepository extends JpaRepository<ProblemReview, Long> {

    Page<ProblemReview> findAllByProblemId(Long ProblemId, Pageable pageable);

    @Query("""
            SELECT SUM(p.evaluation) FROM ProblemReview p
            """)
    Long sumEvaluation();

}
