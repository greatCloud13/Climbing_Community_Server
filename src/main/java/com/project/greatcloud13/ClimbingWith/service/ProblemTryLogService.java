package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ProblemTryLogDTO;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.ProblemTryLog;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.problem.ProblemNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.problemtrylog.ProblemTryLogAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.problemtrylog.ProblemTryLogNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemTryLogRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemTryLogService {

    private final ProblemTryLogRepository problemTryLogRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;

    // 시도 기록 생성
    @Transactional
    public ProblemTryLogDTO createProblemTryLog(Long userId, ProblemTryLogDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Problem problem = problemRepository.findById(request.getProblemId())
                .orElseThrow(ProblemNotFoundException::new);

        ProblemTryLog problemTryLog = ProblemTryLog.builder()
                .user(user)
                .problem(problem)
                .memo(request.getMemo())
                .dropPoint(request.getDropPoint())
                .build();

        problemTryLogRepository.save(problemTryLog);

        return ProblemTryLogDTO.builder()
                .log(problemTryLog)
                .build();
    }

    // 시도 기록 단건 조회
    public ProblemTryLogDTO getProblemTryLog(Long id) {
        ProblemTryLog problemTryLog = problemTryLogRepository.findById(id)
                .orElseThrow(ProblemTryLogNotFoundException::new);

        return ProblemTryLogDTO.builder()
                .log(problemTryLog)
                .build();
    }

    // 사용자별 시도 기록 목록 조회 (최신순)
    public Page<ProblemTryLogDTO> getProblemTryLogsByUser(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Pageable pageable = PageRequest.of(page, size, Sort.by("tryDate").descending());

        return problemTryLogRepository.findAllByUser(user, pageable)
                .map(log -> ProblemTryLogDTO.builder().log(log).build());
    }

    // 시도 기록 수정 (작성자 본인만 가능)
    @Transactional
    public ProblemTryLogDTO updateProblemTryLog(Long id, Long userId, ProblemTryLogDTO request) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ProblemTryLog problemTryLog = problemTryLogRepository.findById(id)
                .orElseThrow(ProblemTryLogNotFoundException::new);

        if (!problemTryLog.getUser().equals(user)) {
            throw new ProblemTryLogAccessDeniedException();
        }

        problemTryLog.updateMemo(request.getMemo());
        problemTryLog.updateDropPoint(request.getDropPoint());

        return ProblemTryLogDTO.builder()
                .log(problemTryLog)
                .build();
    }

    // 시도 기록 삭제 (작성자 본인만 가능)
    @Transactional
    public void deleteProblemTryLog(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ProblemTryLog problemTryLog = problemTryLogRepository.findById(id)
                .orElseThrow(ProblemTryLogNotFoundException::new);

        if (!problemTryLog.getUser().equals(user)) {
            throw new ProblemTryLogAccessDeniedException();
        }

        problemTryLogRepository.delete(problemTryLog);
    }
}
