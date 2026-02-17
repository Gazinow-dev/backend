        package com.gazi.gazi_renew.issue.service.kafka;

        import com.fasterxml.jackson.core.JsonProcessingException;
        import com.gazi.gazi_renew.common.controller.port.KafkaSender;
        import com.gazi.gazi_renew.common.controller.port.RedisUtilService;
        import com.gazi.gazi_renew.common.service.port.ClockHolder;
        import com.gazi.gazi_renew.issue.domain.Issue;
        import com.gazi.gazi_renew.issue.domain.IssueLine;
        import com.gazi.gazi_renew.issue.domain.IssueStation;
        import com.gazi.gazi_renew.issue.domain.enums.IssueKeyword;
        import com.gazi.gazi_renew.issue.domain.enums.KoreanDayOfWeek;
        import com.gazi.gazi_renew.issue.infrastructure.jpa.IssueJpaRepository;
        import com.gazi.gazi_renew.issue.service.port.IssueRepository;
        import com.gazi.gazi_renew.notification.domain.NotificationHistory;
        import com.gazi.gazi_renew.notification.domain.dto.NotificationCreate;
        import com.gazi.gazi_renew.notification.service.port.NotificationHistoryRepository;
        import com.gazi.gazi_renew.route.domain.MyFindRoadStation;
        import com.gazi.gazi_renew.route.domain.MyFindRoadSubPath;
        import com.gazi.gazi_renew.route.service.port.MyFindRoadSubPathRepository;
        import com.gazi.gazi_renew.route.service.port.MyFindRoadSubwayRepository;
        import com.gazi.gazi_renew.station.domain.Line;
        import com.gazi.gazi_renew.station.domain.Station;
        import jakarta.persistence.EntityNotFoundException;
        import lombok.Builder;
        import lombok.RequiredArgsConstructor;
        import lombok.extern.slf4j.Slf4j;
        import org.springframework.kafka.core.KafkaTemplate;
        import org.springframework.stereotype.Component;
        import org.springframework.transaction.annotation.Transactional;

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
            private final IssueRepository issueRepository;
            private final KafkaTemplate<String, NotificationCreate> kafkaTemplate;
            private final MyFindRoadSubPathRepository myFindRoadSubPathRepository;
            private final MyFindRoadSubwayRepository myFindRoadSubwayRepository;
            private final RedisUtilService redisUtilService;
            @Transactional(readOnly = true)
            public void sendNotification(Long issueId, List<IssueLine> lineList, List<IssueStation> stationList) throws JsonProcessingException {
                // 현재 사용자 정보 조회
                Map<String, List<Map<String, Object>>> allUserNotifications = redisUtilService.getAllUserNotifications();
                for (String myFindRoadPathId : allUserNotifications.keySet()) {
                    // 사용자의 알림 조건 조회
                    List<MyFindRoadSubPath> myFindRoadSubPathList = myFindRoadSubPathRepository.findByMyFindRoadPathId(Long.parseLong(myFindRoadPathId));
                    List<Map<String, Object>> notificationsByMyFindRoadPathId = allUserNotifications.get(myFindRoadPathId);

                    List<Line> lines = lineList.stream()
                            .map(IssueLine::getLine)
                            .collect(Collectors.toList());

                    List<Station> stations = stationList.stream()
                            .map(IssueStation::getStation)
                            .toList();

                    Issue issue = issueRepository.findById(issueId)
                            .orElseThrow(() -> new EntityNotFoundException("해당 이슈가 존재하지 않습니다"));

                    boolean routeMatched = matchesRoute(myFindRoadSubPathList, lines, stations);
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
                        .toList();

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
            private boolean matchesNotificationConditions(List<Map<String, Object>> notificationsByMyFindRoadPathId, Issue issue) {
                // 기준: 현재 시간이 아닌 '이슈 시작 시간'
                LocalDateTime issueStartDateTime = issue.getStartDate();
                LocalTime issueStartTime = issueStartDateTime.toLocalTime();

                // 이슈 시작 요일 구하기 (한국어 변환)
                String issueDayInKorean = KoreanDayOfWeek.toKorean(issueStartDateTime.getDayOfWeek());

                log.info("검사 대상 - 이슈 시작 요일: {}, 이슈 시작 시간: {}", issueDayInKorean, issueStartTime);

                for (Map<String, Object> notification : notificationsByMyFindRoadPathId) {
                    // 사용자 알림 설정 값 추출
                    String settingDay = (String) notification.get("day");
                    LocalTime settingFromTime = LocalTime.parse((String) notification.get("from_time"));
                    LocalTime settingToTime = LocalTime.parse((String) notification.get("to_time"));

                    // 1. 요일이 일치하는지 확인
                    if (settingDay.equals(issueDayInKorean)) {
                        // 2. 이슈 시작 시간이 설정된 시간 범위(From ~ To) 안에 있는지 확인
                        // start >= from && start <= to
                        boolean isAfterFrom = !issueStartTime.isBefore(settingFromTime);
                        boolean isBeforeTo = !issueStartTime.isAfter(settingToTime);

                        if (isAfterFrom && isBeforeTo) {
                            log.info("알림 조건 일치! 설정: {} {}~{}", settingDay, settingFromTime, settingToTime);
                            return true;
                        }
                    }
                }

                log.info("일치하는 알림 조건 없음");
                return false;
            }
        }
