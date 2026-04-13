package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.dto.GymSubscribeDTO;
import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.GymSubscribe;
import com.project.greatcloud13.ClimbingWith.entity.User;
import com.project.greatcloud13.ClimbingWith.exception.gym.GymNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.subscribe.SubscribeNotFoundException;
import com.project.greatcloud13.ClimbingWith.exception.user.UserNotFoundException;
import com.project.greatcloud13.ClimbingWith.repository.GymRepository;
import com.project.greatcloud13.ClimbingWith.repository.GymSubscribeRepository;
import com.project.greatcloud13.ClimbingWith.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SubscribeService {

    private final GymSubscribeRepository gymSubscribeRepository;
    private final UserRepository userRepository;
    private final GymRepository gymRepository;

    /**
     * 요청한 사용자가 구독중인 암장 리스트를 조회합니다
     *
     * @param userId 조회하는 사용자 ID
     * @return 구독중인 암장 리스트
     */
    public List<GymSubscribeDTO> findGymSubscribeListByUserId(Long userId){

        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<GymSubscribe> subscribeList = gymSubscribeRepository.findAllByUserANDisActive(user, true);

        return subscribeList.stream().map(GymSubscribeDTO :: from).toList();
    }

    /**
     * 신규 암장 구독 정보를 등록합니다.
     * [Business Rule]
     * 1. 등록하려는 사용자, 암장이 존재해야합니다.
     *
     * @param gymId 구독하는 함장 ID
     * @param userId 등록을 시도하는 사용자 ID
     * @return 등록된 구독 정보
     */
    public GymSubscribeDTO gymSubscribe(Long gymId, Long userId){

//      1. 사용자, 암장 존재 여부 확인
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        Gym gym =gymRepository.findById(gymId)
                .orElseThrow(GymNotFoundException::new);

//      2. 구독 정보 존재 시 구독 상태 변경 후 정보 리턴
        Optional<GymSubscribe> subscribe = gymSubscribeRepository.findByUserAndGym(user, gym);
        if(subscribe.isPresent()){
            subscribe.get().setSubscribe(LocalDateTime.now());

            return GymSubscribeDTO.from(subscribe.get());
        }

//      3. 구독 정보 없을 시 신규 구독 정보 생성 후 정보 리턴
        GymSubscribe newSubscribe = GymSubscribe.builder()
                .user(user)
                .gym(gym)
                .subscribeAt(LocalDateTime.now())
                .build();

        gymSubscribeRepository.save(newSubscribe);
        return GymSubscribeDTO.from(newSubscribe);
    }

    /**
     * 구독 정보 ID를 통해 구독 해지합니다.
     * [Business Rule]
     * 1. 해지하려는 구독 정보가 존재해야합니다.
     *
     * @param subscribeId 구독 정보 ID
     * @return 갱신된 구독 정보
     */
    public GymSubscribeDTO gymUnsubscribe(Long subscribeId){

        GymSubscribe subscribe = gymSubscribeRepository.findById(subscribeId)
                .orElseThrow(SubscribeNotFoundException::new);

        subscribe.unSubscribe(LocalDateTime.now());

        return GymSubscribeDTO.from(subscribe);
    }


}
