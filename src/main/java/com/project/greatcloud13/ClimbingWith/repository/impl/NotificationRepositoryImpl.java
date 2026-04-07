package com.project.greatcloud13.ClimbingWith.repository.impl;

import com.project.greatcloud13.ClimbingWith.repository.NotificationRepository;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import org.yaml.snakeyaml.emitter.Emitter;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class NotificationRepositoryImpl implements NotificationRepository {

    private final Map<Long, SseEmitter> emitters = new ConcurrentHashMap<>();

    @Override
    public void save(Long userId, SseEmitter emitter) {
        emitters.put(userId, emitter);
    }

    @Override
    public void deleteById(Long userId) {
        emitters.remove(userId);
    }

    @Override
    public Optional<SseEmitter> findById(Long userId) {
        SseEmitter emitter = emitters.get(userId);
        if (emitter == null){
            return Optional.empty();
        }
        return Optional.of(emitter);
    }
}
