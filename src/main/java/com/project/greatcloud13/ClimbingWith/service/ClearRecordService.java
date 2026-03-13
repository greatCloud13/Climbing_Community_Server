package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ClearRecordCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordSummaryDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordResponseDTO;
import com.project.greatcloud13.ClimbingWith.dto.ClearRecordUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.*;
import com.project.greatcloud13.ClimbingWith.repository.*;
import com.project.greatcloud13.ClimbingWith.security.CustomUserDetails;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClearRecordService {

    private final ClearRecordRepository clearRecordRepository;
    private final UserRepository userRepository;
    private final ProblemRepository problemRepository;
    private final SectorRepository sectorRepository;
    private final WallSettingRepository settingRepository;


    /**
     * 완등 기록 작성
     * @param userId 사용자 ID
     * @param clearRecordCreateDTO clearRecordCreateDTO
     * @return ClearRecordResponseDTO 
     */
    @Transactional
    public ClearRecordResponseDTO createClearRecord(Long userId, ClearRecordCreateDTO clearRecordCreateDTO){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Problem problem = problemRepository.findById(clearRecordCreateDTO.getProblemId())
                .orElseThrow(()-> new EntityNotFoundException("문제를 찾을 수 없습니다."));

        ClearRecord clearRecord = ClearRecord.builder()
                .user(user)
                .gym(problem.getGym())
                .setting(problem.getSetting())
                .problem(problem)
                .videoUrl(clearRecordCreateDTO.getVideoUrl())
                .build();

        problem.addClearUserCount();
        clearRecordRepository.save(clearRecord);

        return ClearRecordResponseDTO.from(clearRecord);
    }

    /**
     * 사용자 ID 기준 완등 기록 요약
     * @param userId 사용자 ID
     * @param page page
     * @param size size
     * @return Page<ClearRecordSummaryDTO>
     */
    public Page<ClearRecordSummaryDTO> getClearRecordSummaryByUserId(Long userId, int page, int size){

        User user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size);

        Page<ClearRecord> clearRecord = clearRecordRepository.findAllByUserOrderByClearDateDesc(user, pageable);

        return clearRecord.map(ClearRecordSummaryDTO :: from);
    }


    /**
     * 문제 ID 기준 완등 기록 요약(영상 URL이 있는 기록만)
     * @param problemId 문제 ID
     * @param page page
     * @param size size
     * @return Page<ClearRecordSummaryDTO>
     */
    @Transactional
    public Page<ClearRecordSummaryDTO> getClearRecordSummaryByProblemExistVideoUrl(Long problemId, int page, int size){
        Problem problem = problemRepository.findById(problemId)
                .orElseThrow(()-> new EntityNotFoundException("문제를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size);

        Page<ClearRecord> clearRecordPage = clearRecordRepository.findAllByProblemAndVideoUrlIsNotNull(problem, pageable);

        return clearRecordPage.map(ClearRecordSummaryDTO:: from);
    }

    /**
     * 섹터 ID 기준 완등 기록 요약(영상 URL이 있는 기록만)
     * @param sectorId 섹터 ID
     * @param page page
     * @param size size
     * @return Page<ClearRecordSummaryDTO>
     */
    @Transactional
    public Page<ClearRecordSummaryDTO> getClearRecordSummaryBySectorExistVideoUrl(Long sectorId, int page, int size){
        Sector sector = sectorRepository.findById(sectorId)
                .orElseThrow(()-> new EntityNotFoundException("섹터를 찾을 수 없습니다."));

        Pageable pageable = PageRequest.of(page, size);

        Setting setting = settingRepository.findTopBySectorAndIsActiveOrderBySettingDateDesc(sector, true)
                .orElseThrow(()-> new EntityNotFoundException("세팅을 찾을 수 없습니다."));

        Page<ClearRecord> clearRecordPage = clearRecordRepository.findAllBySettingAndVideoUrlIsNotNull(setting, pageable);

        return clearRecordPage.map(ClearRecordSummaryDTO :: from);
    }

    /**
     * 완등 기록 업데이트
     * @param userId 사용자 ID
     * @param clearRecordId 완등 기록 ID
     * @param clearRecordUpdateDTO 완등 기록 Update 정보
     * @return ClearRecordResponseDTO
     */
    @Transactional
    public ClearRecordResponseDTO updateClearRecord(Long userId, Long clearRecordId, ClearRecordUpdateDTO clearRecordUpdateDTO){

        User user = userRepository.findById(userId)
                .orElseThrow(()->new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        
        ClearRecord clearRecord = clearRecordRepository.findById(clearRecordId)
                .orElseThrow(()-> new EntityNotFoundException("기록을 찾을 수 없습니다"));

        if(user.getRole()!=Role.ADMIN && !clearRecord.getUser().equals(user)){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }
        
        if(clearRecord.getId().equals(clearRecordUpdateDTO.getProblemId())){
            Problem problem = problemRepository.findById(clearRecordUpdateDTO.getProblemId())
                    .orElseThrow(()-> new EntityNotFoundException("문제를 찾을 수 없습니다."));
            clearRecord.updateProblem(problem);
        }
        if(!clearRecord.getVideoUrl().equals(clearRecordUpdateDTO.getVideoUrl())){
            clearRecord.updateVideoUrl(clearRecord.getVideoUrl());
        }
        return ClearRecordResponseDTO.from(clearRecord);
    }

    /**
     * 완등 기록 영구삭제
     * @param userId 사용자 ID
     * @param id 완등 기록 ID
     */
    @Transactional
    public void deleteClearRecord(Long userId, Long clearRecordId){
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        ClearRecord clearRecord = clearRecordRepository.findById(clearRecordId)
                .orElseThrow(()->new EntityNotFoundException("기록을 찾을 수 없습니다."));

        if(user.getRole()!=Role.ADMIN && !clearRecord.getUser().equals(user)){
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        clearRecord.getProblem().subClearUserCount();

        clearRecordRepository.delete(clearRecord);
    }

}
