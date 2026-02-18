package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemReviewUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.ProblemReview;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemReviewRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProblemReviewService {

    private final ProblemReviewRepository problemReviewRepository;
    private final ProblemRepository problemRepository;
    private final UserRepository userRepository;

    @Transactional
    public ProblemReviewDTO createReview(ProblemReviewCreateDTO request){

        User user = userRepository.findById(request.getUserId())
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다"));

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(()->new EntityNotFoundException("문제를 찾을 수 없습니다"));

        ProblemReview problemReview = ProblemReview.builder()
                .problem(problem)
                .user(user)
                .problemHint(request.getProblemHint())
                .evaluation(request.getEvaluation())
                .build();

        ProblemReviewDTO result = ProblemReviewDTO.from(problemReviewRepository.save(problemReview));

        // 평점 재계산
        Long totalEvaluation = problemReviewRepository.sumEvaluation();
        Long count = problemReviewRepository.count();

        problem.updateEvaluation((float) (totalEvaluation/count));

        return result;
    }

    @Transactional(readOnly = true)
    public ProblemReviewDTO getReview(Long id){
        ProblemReview problemReview = problemReviewRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("리뷰를 찾을 수 없습니다"));

        return ProblemReviewDTO.from(problemReview);
    }

    @Transactional(readOnly = true)
    public Page<ProblemReviewDTO> getReviewByProblemId(int page, Long problemId){

        Pageable pageable = PageRequest.of(page,10, Sort.by("createdAt").descending());

        return problemReviewRepository.findAllByProblemId(problemId, pageable).map(ProblemReviewDTO::from);
    }

    @Transactional
    public ProblemReviewDTO updateReview(Long id, ProblemReviewUpdateDTO request){

        ProblemReview problemReview = problemReviewRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("리뷰를 찾을 수 없습니다"));

        problemReview.update(request.getProblemHint(), request.getEvaluation());

        // 평점 재계산
        Long totalEvaluation = problemReviewRepository.sumEvaluation();
        Long count = problemReviewRepository.count();
        problemReview.getProblem().updateEvaluation((float) (totalEvaluation/count));

        return ProblemReviewDTO.from(problemReview);
    }

    public void deleteReview(Long id){
        problemReviewRepository.deleteById(id);
    }

}
