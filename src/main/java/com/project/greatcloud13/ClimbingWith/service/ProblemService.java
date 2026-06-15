package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.ProblemCreateDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemDTO;
import com.project.greatcloud13.ClimbingWith.dto.ProblemUpdateDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.GymLevel;
import com.project.greatcloud13.ClimbingWith.entity.Problem;
import com.project.greatcloud13.ClimbingWith.entity.Setting;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymAccessDeniedException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymLevelNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.problem.ProblemNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.setting.SettingNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymLevelRepository;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.ProblemRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import com.project.greatcloud13.ClimbingWith.repository.WallSettingRepository;
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
    private final UserRepository userRepository;

    @Transactional
    public ProblemDTO createProblem(ProblemCreateDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Setting setting = settingRepository.findById(request.getSettingId())
                .orElseThrow(SettingNotFoundException::new);

        if (!user.gymValidate(setting.getGym())) {
            throw new GymAccessDeniedException();
        }

        GymLevel gymLevel = gymLevelRepository.findById(request.getGymLevelId())
                .orElseThrow(GymLevelNotFoundException::new);

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

    public ProblemDTO getProblem(Long id) {
        return ProblemDTO.from(problemRepository.findById(id)
                .orElseThrow(ProblemNotFoundException::new));
    }

    public List<ProblemDTO> getProblemByGymLevel(Long id) {
        GymLevel gymLevel = gymLevelRepository.findById(id)
                .orElseThrow(GymLevelNotFoundException::new);

        return problemRepository.findAllByGymLevel(gymLevel).stream()
                .map(ProblemDTO::from)
                .toList();
    }

    public Page<ProblemDTO> getProblemPageByGym(Long id, int page) {
        Gym gym = gymRepository.findById(id)
                .orElseThrow(GymNotFoundException::new);

        List<Setting> settingList = settingRepository.findAllByGymAndIsActive(gym, true);

        if (settingList.isEmpty()) {
            throw new SettingNotFoundException();
        }

        Pageable pageable = PageRequest.of(page, 10);

        return problemRepository.findAllBySettingIn(settingList, pageable)
                .map(ProblemDTO::from);
    }

    public List<ProblemDTO> getProblemListBySetting(Long settingId) {
        return problemRepository.findAllBySettingId(settingId).stream()
                .map(ProblemDTO::from)
                .toList();
    }

    @Transactional
    public ProblemDTO updateProblem(Long id, ProblemUpdateDTO request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Problem problem = problemRepository.findById(id)
                .orElseThrow(ProblemNotFoundException::new);

        if (!user.gymValidate(problem.getGym())) {
            throw new GymAccessDeniedException();
        }

        GymLevel gymLevel = gymLevelRepository.findById(request.getGymLevelId())
                .orElseThrow(GymLevelNotFoundException::new);

        problem.update(request.getTitle(), request.getProblemType(), gymLevel, request.getDescription());

        return ProblemDTO.from(problem);
    }

    @Transactional
    public void deleteProblem(Long id, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Problem problem = problemRepository.findById(id)
                .orElseThrow(ProblemNotFoundException::new);

        if (!user.gymValidate(problem.getGym())) {
            throw new GymAccessDeniedException();
        }

        problemRepository.deleteById(id);
    }
}
