package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ProblemCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import com.project.greatcloud13.ClimbingWith.repository.GymLevelRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.WallSettingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final GymLevelRepository gymLevelRepository;
    private final WallSettingRepository settingRepository;

    @Transactional
    public ProblemDTO createProblem(ProblemCreateDTO request){
        Setting setting = settingRepository.findById(request.getSettingId())
                .orElseThrow(()->new EntityNotFoundException("세팅을 찾을 수 없습니다."));

        GymLevel gymLevel = gymLevelRepository.findById(request.getGymLevelId())
                .orElseThrow(()->new EntityNotFoundException("레벨을 찾을 수 없습니다."));

        Problem problem = Problem.builder()
                .setting(setting)
                .title(request.getTitle())
                .problemType(request.getProblemType())
                .gymLevel(gymLevel)
                .description(request.getDescription())
                .build();

        return ProblemDTO.from(problemRepository.save(problem));
    }

    @Transactional(readOnly = true)
    public ProblemDTO getProblem(Long id){

        return ProblemDTO.from(problemRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("문제를 찾을 수 없습니다.")));
    }

    @Transactional(readOnly = true)
    public List<ProblemDTO> getProblemListBySetting(Long settingId){

        return problemRepository.findAllBySettingId(settingId).stream().map(ProblemDTO :: from).toList();
    }

    @Transactional
    public ProblemDTO updateProblem(Long id, ProblemUpdateDTO request){
        Problem problem = problemRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("문제를 찾을 수 없습니다."));

        GymLevel gymLevel = gymLevelRepository.findById(request.getGymLevelId())
                        .orElseThrow(()->new EntityNotFoundException("레벨을 찾을 수 없습니다."));

        problem.update(request.getTitle(), request.getProblemType(), gymLevel, request.getDescription());

        return ProblemDTO.from(problem);
    }

    @Transactional
    public void deleteProblem(Long id){
        problemRepository.deleteById(id);
    }
}
