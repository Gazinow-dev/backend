package com.gazi.gazi_renew.issue.service.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.gazi.gazi_renew.common.controller.port.KafkaSender;
import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.issue.domain.Issue;
import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
import com.gazi.gazi_renew.issue.domain.enums.KoreanDayOfWeek;
import com.gazi.gazi_renew.notification.domain.NotificationHistory;
import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
import com.gazi.gazi_renew.notification.service.port.NotificationHistoryRepository;
import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
import com.gazi.gazi_renew.route.service.port.MyFindRoadSubwayRepository;
import com.gazi.gazi_renew.station.domain.Line;
import com.gazi.gazi_renew.station.domain.Station;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Builder
@Component
@RequiredArgsConstructor
public class NotificationSender implements KafkaSender {
    private final KafkaTemplate<String, NotificationCreate> kafkaTemplate;
    private final MyFindRoadSubPathRepository myFindRoadSubPathRepository;
    private final MyFindRoadSubwayRepository myFindRoadSubwayRepository;
    private final RedisUtilService redisUtilService;

    public void sendNotification(Issue issue, List<Line> lineList, List<Station> stationList) throws JsonProcessingException {
        // 현재 사용자 정보 조회
        Map<String, List<Map<String, Object>>> allUserNotifications = redisUtilService.getAllUserNotifications();
        for (String myFindRoadPathId : allUserNotifications.keySet()) {
            // 사용자의 알림 조건 조회
            List<MyFindRoadSubPath> myFindRoadSubPathList = myFindRoadSubPathRepository.findByMyFindRoadPathId(Long.parseLong(myFindRoadPathId));
            List<Map<String, Object>> notificationsByMyFindRoadPathId = allUserNotifications.get(myFindRoadPathId);

            boolean routeMatched = matchesRoute(myFindRoadSubPathList, lineList, stationList);
            boolean conditionMatched = matchesNotificationConditions(notificationsByMyFindRoadPathId, issue);

            if (routeMatched) {
                // 조건까지 일치하면 FCM 발송 (Kafka로 전송)
                if (conditionMatched) {
                    NotificationCreate notificationCreate = NotificationCreate.builder()
                            .myRoadId(Long.parseLong(myFindRoadPathId))
                            .issueId(issue.getId())
                            .sendNotification(Boolean.TRUE)
                            .build();

                    kafkaTemplate.send("notification", notificationCreate);
                    log.info("Kafka 토픽 'notification'에 알림 전송 완료 - roadId: {}, issueId: {}", myFindRoadPathId, issue.getId());
                } else {
                    NotificationCreate notificationCreate = NotificationCreate.builder()
                            .myRoadId(Long.parseLong(myFindRoadPathId))
                            .issueId(issue.getId())
                            .sendNotification(Boolean.FALSE)
                            .build();
                    kafkaTemplate.send("notification", notificationCreate);
                }
            }
        }

    }
    public boolean matchesRoute(List<MyFindRoadSubPath> myFindRoadSubPathList, List<Line> lineList, List<Station> stationList) {
        HashSet<String> myFindRoadStationList = new HashSet<>();
        HashSet<String> myFindRoadLineList = new HashSet<>();

        List<String> lineNameList = lineList.stream()
                .map(Line::getLineName)
                .collect(Collectors.toList());
        List<String> stationNameList = stationList.stream()
                .map(Station::getName)
                .collect(Collectors.toList());

        for (MyFindRoadSubPath subPath : myFindRoadSubPathList) {
            myFindRoadLineList.add(subPath.getName());
            List<MyFindRoadStation> stations = myFindRoadSubwayRepository.findAllByMyFindRoadSubPathId(subPath.getId());
            for (MyFindRoadStation station : stations) {
                myFindRoadStationList.add(station.getStationName());
            }
        }
        log.info("내 경로의 호선 리스트: {}, 이슈의 호선 리스트: {}", myFindRoadLineList, lineNameList);
        // 내 경로와 이슈 호선이 겹치는지 확인
        if (Collections.disjoint(myFindRoadLineList, lineNameList)) {
            log.warn("내 경로와 이슈의 호선이 겹치지 않습니다.");
            return false;
        }
        // 내 경로와 이슈 지하철역이 겹치는지 확인
        if (Collections.disjoint(myFindRoadStationList, stationNameList)) {
            log.warn("내 경로와 이슈의 지하철역이 겹치지 않습니다.");
            return false;
        }
        // 둘 다 겹치면 true 반환
        return true;
    }
    private boolean matchesNotificationConditions(List<Map<String, Object>> notificationsByMyFindRoadPathId ,Issue issue) throws JsonProcessingException {
        // 현재 시간과 요일 가져오기
        LocalDateTime now = LocalDateTime.now();
        DayOfWeek currentDay = now.getDayOfWeek();
        String currentDayInKorean = KoreanDayOfWeek.toKorean(currentDay);
        log.info("현재 요일: {}, 현재 시간: {}", currentDayInKorean, now.toLocalTime());
        if (!issue.getStartDate().isAfter(now) && !issue.getExpireDate().isBefore(now)) {
            for (Map<String, Object> notification : notificationsByMyFindRoadPathId) {
                // 알림 조건 추출
                String day = (String) notification.get("day");// 요일 조건 리스트
                LocalTime fromTime = LocalTime.parse((String) notification.get("from_time")); // 알림 시작 시간
                LocalTime toTime = LocalTime.parse((String) notification.get("to_time")); // 알림 종료 시간
                log.info("알림 조건 - 요일: {}, 시작 시간: {}, 종료 시간: {}", day, fromTime, toTime);
                // 현재 요일 조건 확인
                if (day.equals(currentDayInKorean)) {
                    // 알림 시간 조건이 이슈 기간에 포함되는지 확인
                    boolean isFromTimeValid = !fromTime.isAfter(now.toLocalTime());
                    boolean isToTimeValid = !toTime.isBefore(now.toLocalTime());
                    if (!isFromTimeValid) {
                        log.warn("현재 시간이 알림 시작 시간보다 이전입니다.");
                    }
                    if (!isToTimeValid) {
                        log.warn("현재 시간이 알림 종료 시간보다 이후입니다.");
                    }

                    if (isFromTimeValid && isToTimeValid) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
