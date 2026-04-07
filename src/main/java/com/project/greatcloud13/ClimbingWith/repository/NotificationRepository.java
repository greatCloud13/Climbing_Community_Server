package com.project.greatcloud13.ClimbingWith.repository;

import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

public interface NotificationRepository {

    void save(Long userId, SseEmitter emitter);

    void deleteById(Long userId);

    Optional<SseEmitter> findById(Long userId);
}
