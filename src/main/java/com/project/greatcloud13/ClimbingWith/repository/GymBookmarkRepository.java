package com.project.greatcloud13.ClimbingWith.repository;

import com.project.greatcloud13.ClimbingWith.entity.GymBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GymBookmarkRepository extends JpaRepository<GymBookmark, Long> {

    List<GymBookmark> findAllByUser_IdOrderByIdDesc(Long userId);
}
