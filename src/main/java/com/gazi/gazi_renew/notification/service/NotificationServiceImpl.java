package com.gazi.gazi_renew.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.notification.service.port.NotificationRepository;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.notification.controller.port.NotificationService;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final MyFindRoadService myFindRoadService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final RedisUtilService redisUtilService;
    /**
     * 알림 설정 변경 메서드
     * myFindRoadService의 updateRouteNotification메세드를 여기서 호출해서
     * 하나의 트랜잭션으로 묶기
     * @param : MyFindRoadNotificationRequest myFindRoadNotification
     */
    @Transactional
    public List<Notification> saveNotificationTimes(MyFindRoadNotificationCreate myFindRoadNotificationCreate) throws JsonProcessingException {

        MyFindRoad myFindRoad = myFindRoadPathRepository.findById(myFindRoadNotificationCreate.getMyPathId()).orElseThrow(
                () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
        );

        List<Notification> notificationList = Notification.from(myFindRoadNotificationCreate, myFindRoad);

        // 알림 시간을 저장한 후 경로 알림 설정을 업데이트
        notificationRepository.saveAll(notificationList);
        myFindRoadService.updateRouteNotification(myFindRoadNotificationCreate.getMyPathId(), true);
        redisUtilService.saveNotificationTimes(notificationList, myFindRoad);
        return notificationList;
    }

    public List<Notification> getNotificationTimes(Long myPathId) {
        List<Notification> notificationList = notificationRepository.findByMyFindRoadPathId(myPathId);
        return notificationList;
    }

    @Transactional
    public void deleteNotificationTimes(Long myPathId) {
        MyFindRoad myFindRoad = myFindRoadPathRepository.findById(myPathId).orElseThrow(
                () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
        );
        notificationRepository.deleteByMyFindRoad(myFindRoad);

        String fieldName = myFindRoad.getMember().getId().toString();

        // redis에 데이터도 삭제
        redisUtilService.deleteNotification(fieldName);
    }

    @Transactional
    public List<Notification> updateNotificationTimes(MyFindRoadNotificationCreate myFindRoadNotificationCreate) throws JsonProcessingException {
        // 요청으로 받은 경로에 대한 알림 찾기
        MyFindRoad myFindRoad = myFindRoadPathRepository.findById(myFindRoadNotificationCreate.getMyPathId())
                .orElseThrow(() -> new EntityNotFoundException("해당 경로가 존재하지 않습니다."));
        //알림 모두 삭제 후 다시 저장
        deleteNotificationTimes(myFindRoadNotificationCreate.getMyPathId());
        List<Notification> notificationList = Notification.from(myFindRoadNotificationCreate, myFindRoad);

        // 알림 다시 저장
        notificationRepository.saveAll(notificationList);

        String fieldName = myFindRoad.getMember().getId().toString();
        // Redis의 기존 값을 삭제
        redisUtilService.deleteNotification(fieldName);
        redisUtilService.saveNotificationTimes(notificationList, myFindRoad);
        return notificationList;
    }

    @Override
    public Long getPathId(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 알림이 존재하지 않습니다."));
        Long myPathId = notification.getMyFindRoad().getId();
        return myPathId;
    }
}