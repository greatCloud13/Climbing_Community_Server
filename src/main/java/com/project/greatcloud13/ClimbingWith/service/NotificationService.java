package com.project.greatcloud13.ClimbingWith.service;

import com.project.greatcloud13.ClimbingWith.exception.common.NotImplementedException;
import com.project.greatcloud13.ClimbingWith.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;

    /**
     * 요청한 ID를 통해 SseEmitter를 생성하고 저장
     *
     * @param userId 사용자 ID
     * @return 생성된 SseEmitter 객체
     */
    public SseEmitter subscribe(Long userId){

//      1. 타임아웃 설정을 포함한 Emitter 생성 (기본 30초, 현재 코드에서는 1시간으로 설정)
        SseEmitter emitter = new SseEmitter(60 * 60 * 1000L);

//      2. 연결 종료/타임 아웃시 삭제 처리
        emitter.onCompletion(()-> notificationRepository.deleteById(userId));
        emitter.onTimeout(()-> notificationRepository.deleteById(userId));
        notificationRepository.save(userId, emitter);

//      3. 더미 데이터 전송 (연결 직후 데이터가 없으면 503 에러 발생 가능성 있음)
        sendToClient(userId, "연결 성공! [userId=" + userId +"]");

        return emitter;
    }

    /**
     * SseEmitter를 통해 data를 전송
     *
     * @param userId 사용자 ID
     * @param data 전송할 데이터
     */
    public void sendNotification(Long userId, Object data){
        sendToClient(userId, data);
    }

    /**
     * Emitter의 상태를 체크 후 알림 발송
     * todo: emitter 조회시 Null일 경우 FCM 토큰을 통해 푸시알림을 전송하는 기능 개발 필요
     *
     * @param userId 사용자 ID
     * @param data 전송할 데이터
     * @throws NotImplementedException emitter 조회시 Null인 경우
     */
    private void sendToClient(Long userId, Object data){
        SseEmitter emitter = notificationRepository.findById(userId)
                .orElseThrow(NotImplementedException::new);
        try{
            emitter.send(SseEmitter.event()
                    .id(String.valueOf(userId))
                    .name("notification")
                    .data(data));
        }catch (IOException e){
            notificationRepository.deleteById(userId);
        }
    }

}
