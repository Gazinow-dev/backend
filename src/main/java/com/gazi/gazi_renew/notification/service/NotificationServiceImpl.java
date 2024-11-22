package com.gazi.gazi_renew.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.common.controller.port.SecurityUtilService;
import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.issue.service.port.IssueRepository;
import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.member.service.port.MemberRepository;
import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import com.gazi.gazi_renew.notification.service.port.NotificationHistoryRepository;
import com.gazi.gazi_renew.notification.service.port.NotificationRepository;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.notification.controller.port.NotificationService;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import com.gazi.gazi_renew.route.service.port.MyFindRoadPathRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Slf4j

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    private final NotificationRepository notificationRepository;
    private final NotificationHistoryRepository notificationHistoryRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final IssueRepository issueRepository;
    private final MemberRepository memberRepository;
    private final SecurityUtilService securityUtilService;
    private final RedisUtilService redisUtilService;
    /**
     * 알림 설정 변경 메서드
     * myFindRoadService의 updateRouteNotification메세드를 여기서 호출해서
     * 하나의 트랜잭션으로 묶기
     * @param : MyFindRoadNotificationRequest myFindRoadNotification
     */
    @Override
        public void saveNotificationTimes(MyFindRoadNotificationCreate myFindRoadNotificationCreate) throws JsonProcessingException {
        try {
            MyFindRoad myFindRoad = myFindRoadPathRepository.findById(myFindRoadNotificationCreate.getMyPathId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
            );

            List<Notification> notificationList = Notification.from(myFindRoadNotificationCreate, myFindRoad.getId());

            // 알림 시간을 저장한 후 경로 알림 설정을 업데이트
            notificationRepository.saveAll(notificationList);

            myFindRoad = myFindRoad.updateNotification(true);
            myFindRoadPathRepository.updateNotification(myFindRoad);

            redisUtilService.saveNotificationTimes(notificationList, myFindRoad);
        } catch (DataIntegrityViolationException e) {
            throw ErrorCode.throwDuplicateNotificationForDay();
        }
    }
    @Override
    @Transactional(readOnly = true)
    public List<Notification> getNotificationTimes(Long myPathId) {
        List<Notification> notificationList = notificationRepository.findByMyFindRoadPathId(myPathId);
        return notificationList;
    }
    @Override
    public void deleteNotificationTimes(Long myPathId) {
        MyFindRoad myFindRoad = myFindRoadPathRepository.findById(myPathId).orElseThrow(
                () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
        );
        notificationRepository.deleteByMyFindRoad(myFindRoad);

        String fieldName = myFindRoad.getMemberId().toString();

        // redis에 데이터도 삭제
        redisUtilService.deleteNotification(fieldName);
    }
    @Override
    public List<Notification> updateNotificationTimes(MyFindRoadNotificationCreate myFindRoadNotificationCreate) throws JsonProcessingException {
        // 요청으로 받은 경로에 대한 알림 찾기
        MyFindRoad myFindRoad = myFindRoadPathRepository.findById(myFindRoadNotificationCreate.getMyPathId())
                .orElseThrow(() -> new EntityNotFoundException("해당 경로가 존재하지 않습니다."));
        //알림 모두 삭제 후 다시 저장
        deleteNotificationTimes(myFindRoadNotificationCreate.getMyPathId());
        List<Notification> notificationList = Notification.from(myFindRoadNotificationCreate, myFindRoad.getId());

        // 알림 다시 저장
        notificationList = notificationRepository.saveAll(notificationList);

        String fieldName = myFindRoad.getMemberId().toString();
        // Redis의 기존 값을 삭제
        redisUtilService.deleteNotification(fieldName);
        redisUtilService.saveNotificationTimes(notificationList, myFindRoad);
        return notificationList;
    }

    @Override
    public Long getPathId(Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new EntityNotFoundException("해당 알림이 존재하지 않습니다."));
        Long myPathId = notification.getMyFindRoadPathId();
        return myPathId;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationHistory> findAllByMemberId(Pageable pageable) {
        Member member = memberRepository.getReferenceByEmail(securityUtilService.getCurrentUserEmail()).orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));
        return notificationHistoryRepository.findAllByMemberId(member.getId(), pageable);
    }

    @Override
    public void markAsRead(Long notificationId) {
        notificationHistoryRepository.updateNotificationIsRead(notificationId);
    }
}