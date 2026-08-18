package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.*;
import java.time.LocalDate;
import java.util.Objects;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.exception.clearrecord.ClearRecordAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.clearrecord.ClearRecordNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.problem.ProblemNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.sector.SectorNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.setting.SettingNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ClearRecordService {

    private final ClearRecordRepository clearRecordRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SectorRepository sectorRepository;
    private final WallSettingRepository settingRepository;
    private final GymRepository gymRepository;
    private final ClearRecordRepositoryCustom clearRecordRepositoryCustom;

    // 트라이 시작: isClear=false, startDate=등록일, clearDate=null 상태로 등록됩니다.
    @Transactional
    public ClearRecordResponseDTO createClearRecord(Long userId, ClearRecordCreateDTO clearRecordCreateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Problem problem = problemRepository.findById(clearRecordCreateDTO.getProblemId())
                .orElseThrow(ProblemNotFoundException::new);

        ClearRecord clearRecord = ClearRecord.builder()
                .user(user)
                .gym(problem.getGym())
                .setting(problem.getSetting())
                .problem(problem)
                .videoUrl(clearRecordCreateDTO.getVideoUrl())
                .build();

        clearRecordRepository.save(clearRecord);

        return ClearRecordResponseDTO.from(clearRecord);
    }

    // 클리어 처리: 진행중이던 트라이 기록을 isClear=true, clearDate 등록 상태로 갱신합니다.
    @Transactional
    public ClearRecordResponseDTO clearProblem(Long userId, Long clearRecordId, LocalDate clearDate) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ClearRecord clearRecord = clearRecordRepository.findById(clearRecordId)
                .orElseThrow(ClearRecordNotFoundException::new);

        if (user.getRole() != Role.ADMIN && !clearRecord.getUser().equals(user)) {
            throw new ClearRecordAccessDeniedException();
        }

        if (!clearRecord.isClear()) {
            clearRecord.getProblem().addClearUserCount();
        }

        clearRecord.markClear(clearDate != null ? clearDate : LocalDate.now());

        return ClearRecordResponseDTO.from(clearRecord);
    }

    public Page<ClearRecordSummaryDTO> getClearRecordSummaryByUserId(Long userId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Pageable pageable = PageRequest.of(page, size);

        return clearRecordRepository.findAllByUserAndIsClearTrueAndIsActiveTrueOrderByClearDateDesc(user, pageable)
                .map(ClearRecordSummaryDTO::from);
    }

    public Page<ClearRecordSummaryDTO> getClearRecordSummaryByUserIdAndGym(Long userId, Long gymId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

        Pageable pageable = PageRequest.of(page, size);

        return clearRecordRepository.findAllByUserAndGymAndIsClearTrueAndIsActiveTrueOrderByClearDateDesc(user, gym, pageable)
                .map(ClearRecordSummaryDTO::from);
    }

    public Page<ClearRecordSummaryDTO> getClearRecordSummaryByUserIdAndSettingId(Long userId, Long settingId, int page, int size) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Setting setting = settingRepository.findById(settingId)
                .orElseThrow(SettingNotFoundException::new);

        Pageable pageable = PageRequest.of(page, size);

        return clearRecordRepository.findAllByUserAndSettingAndIsClearTrueAndIsActiveTrueOrderByClearDateDesc(user, setting, pageable)
                .map(ClearRecordSummaryDTO::from);
    }

    public Page<ClearRecordSummaryDTO> getClearRecordSummaryByProblemExistVideoUrl(Long problemId, int page, int size) {
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);

        Pageable pageable = PageRequest.of(page, size);

        return clearRecordRepository.findAllByProblemAndVideoUrlIsNotNullAndIsClearTrueAndIsActiveTrue(problem, pageable)
                .map(ClearRecordSummaryDTO::from);
    }

    public Page<ClearRecordSummaryDTO> getClearRecordSummaryBySectorExistVideoUrl(Long sectorId, int page, int size) {
        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(SectorNotFoundException::new);

        Setting setting = settingRepository.findTopBySectorAndIsActiveOrderBySettingDateDesc(sector, true)
                .orElseThrow(SettingNotFoundException::new);

        Pageable pageable = PageRequest.of(page, size);

        return clearRecordRepository.findAllBySettingAndVideoUrlIsNotNullAndIsClearTrueAndIsActiveTrue(setting, pageable)
                .map(ClearRecordSummaryDTO::from);
    }

    // user+problem 기준 진행 중(isClear=false, isActive=true)인 트라이 기록을 조회합니다.
    public ClearRecordResponseDTO getInProgressClearRecord(Long userId, Long problemId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(ProblemNotFoundException::new);

        ClearRecord clearRecord = clearRecordRepository
                .findFirstByUserAndProblemAndIsClearFalseAndIsActiveTrueOrderByStartDateDesc(user, problem)
                .orElseThrow(ClearRecordNotFoundException::new);

        return ClearRecordResponseDTO.from(clearRecord);
    }

    @Transactional
    public ClearRecordResponseDTO updateClearRecord(Long userId, Long clearRecordId, ClearRecordUpdateDTO clearRecordUpdateDTO) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ClearRecord clearRecord = clearRecordRepository.findById(clearRecordId)
                .orElseThrow(ClearRecordNotFoundException::new);

        if (user.getRole() != Role.ADMIN && !clearRecord.getUser().equals(user)) {
            throw new ClearRecordAccessDeniedException();
        }
        
        if(!clearRecord.getProblem().getId().equals(clearRecordUpdateDTO.getProblemId())){
            Problem problem = problemRepository.findById(clearRecordUpdateDTO.getProblemId())
                    .orElseThrow(ProblemNotFoundException::new);
            clearRecord.updateProblem(problem);
        }
        if(!Objects.equals(clearRecordUpdateDTO.getVideoUrl(), clearRecord.getVideoUrl())){
            clearRecord.updateVideoUrl(clearRecordUpdateDTO.getVideoUrl());
        }

        return ClearRecordResponseDTO.from(clearRecord);
    }

    public ClearRecordStatisticsResponse getClearRecordStatistics(Long userId, Long gymId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Gym gym = gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

        ClearRecordStatistics statistics = clearRecordRepositoryCustom.getStatistics(user, gym);

        return ClearRecordStatisticsResponse.from(statistics);
    }

    @Transactional
    public void deleteClearRecord(Long userId, Long clearRecordId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        ClearRecord clearRecord = clearRecordRepository.findById(clearRecordId)
                .orElseThrow(ClearRecordNotFoundException::new);

        if (user.getRole() != Role.ADMIN && !clearRecord.getUser().equals(user)) {
            throw new ClearRecordAccessDeniedException();
        }

        if (clearRecord.isClear()) {
            clearRecord.getProblem().subClearUserCount();
        }
        clearRecordRepository.delete(clearRecord);
    }
}
