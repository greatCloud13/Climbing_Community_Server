package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.GymSubscribe;
import com.project.greatcloud13.ClimbingWith.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GymSubscribeRepository extends JpaRepository<GymSubscribe, Long> {

    Optional<GymSubscribe> findByUserAndGym(User user, Gym gym);

    List<GymSubscribe> findAllByUserANDisActive(User user, Boolean isActive);

    @Query("SELECT u.user.id FROM GymSubscribe u WHERE u.gym.id=:gymId AND isActive=true")
    List<Long> findSubscribedUserIds(@Param("gymId") Long gymId);


}
