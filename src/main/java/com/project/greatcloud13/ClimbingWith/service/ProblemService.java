package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ProblemCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import com.project.greatcloud13.ClimbingWith.repository.GymLevelRepository;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.WallSettingRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProblemService {

    private final ProblemRepository problemRepository;
    private final GymLevelRepository gymLevelRepository;
    private final GymRepository gymRepository;
    private final WallSettingRepository settingRepository;

    @Transactional
    public ProblemDTO createProblem(ProblemCreateDTO request){
        Setting setting = settingRepository.findById(request.getSettingId())
                .orElseThrow(()->new EntityNotFoundException("세팅을 찾을 수 없습니다."));

        GymLevel gymLevel = gymLevelRepository.findById(request.getGymLevelId())
                .orElseThrow(()->new EntityNotFoundException("레벨을 찾을 수 없습니다."));

        Problem problem = Problem.builder()
                .setting(setting)
                .gym(setting.getGym())
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
    public List<ProblemDTO> getProblemByGymLevel(Long id){
        GymLevel gymLevel = gymLevelRepository.findById(id)
                .orElseThrow(()-> new EntityNotFoundException("해당하는 레벨을 찾을 수 없습니다."));

        List<Problem> problemDTOList = problemRepository.findAllByGymLevel(gymLevel);

        return problemDTOList.stream().map(ProblemDTO :: from).toList();
    }

    public Page<ProblemDTO> getProblemPageByGym(Long id, int page){
        Gym gym = gymRepository.findById(id)
                .orElseThrow(()->new EntityNotFoundException("암장을 찾을 수 없습니다."));

        List<Setting> settingList = settingRepository.findAllByGymAndIsActive(gym,true);

        if(settingList.isEmpty()){
            throw new EntityNotFoundException("활성화된 세팅이 없습니다.");
        }

        Pageable pageable = PageRequest.of(page, 10);

        Page<Problem> problemPage = problemRepository.findAllBySettingIn(settingList, pageable);

        return problemPage.map(ProblemDTO::from);
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
