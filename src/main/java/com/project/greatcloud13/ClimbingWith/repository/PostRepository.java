package com.project.greatcloud13.ClimbingWith.repository;


import com.project.greatcloud13.ClimbingWith.entity.Gym;
import com.project.greatcloud13.ClimbingWith.entity.Post;
import com.project.greatcloud13.ClimbingWith.entity.PostType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.stream.Stream;

public interface PostRepository extends JpaRepository<Post, Long> {

    Page<Post> findAllByGym(Gym gym, Pageable pageable);

    Page<Post> findAllByGymAndPostType(Gym gym, PostType postType, Pageable pageable);

    Stream<Post> streamAllBy();

}
