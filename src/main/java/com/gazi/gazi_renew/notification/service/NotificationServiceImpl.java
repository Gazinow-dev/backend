package com.gazi.gazi_renew.notification.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPath;
import com.gazi.gazi_renew.notification.controller.port.NotificationService;
import com.gazi.gazi_renew.notification.infrastructure.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoadNotificationRequest;
import com.gazi.gazi_renew.route.controller.response.MyFindRoadNotificationResponse;
import com.gazi.gazi_renew.common.controller.response.Response;
import com.gazi.gazi_renew.route.infrastructure.MyFindRoadPathRepository;
import com.gazi.gazi_renew.notification.infrastructure.NotificationRepository;
import com.gazi.gazi_renew.route.controller.port.MyFindRoadService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.*;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService {
    private final Response response;
    private final NotificationRepository notificationRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;
    private final MyFindRoadService myFindRoadService;
    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper = new ObjectMapper();
    /**
     * 알림 설정 변경 메서드
     * myFindRoadService의 updateRouteNotification메세드를 여기서 호출해서
     * 하나의 트랜잭션으로 묶기
     * @param : MyFindRoadNotificationRequest request
     */
    @Transactional
    public ResponseEntity<Response.Body> saveNotificationTimes(MyFindRoadNotificationRequest request) {
        try {
            List<Notification> savedTimes = new ArrayList<>();
            MyFindRoadPath myPath = myFindRoadPathRepository.findById(request.getMyPathId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
            );
            List<Map<String, Object>> notificationJsonList = new ArrayList<>();

            // 각 DayTimeRange에 대해 Notification 객체 생성 및 저장
            for (MyFindRoadNotificationRequest.DayTimeRange dayTimeRange : request.getDayTimeRanges()) {
                String day = dayTimeRange.getDay();
                LocalTime fromTime = LocalTime.parse(dayTimeRange.getFromTime());
                LocalTime toTime = LocalTime.parse(dayTimeRange.getToTime());

                // 시간 검증 로직 추가
                if (fromTime.isAfter(toTime) || fromTime.equals(toTime)) {
                    throw new IllegalArgumentException("fromTime은 toTime보다 이전이어야 합니다: "
                            + "fromTime=" + fromTime + ", toTime=" + toTime);
                }

                // 'from' 및 'to' 시간 설정 후 Notification 객체 생성
                Notification notification = Notification.builder()
                        .dayOfWeek(day)
                        .fromTime(fromTime)
                        .toTime(toTime)
                        .myFindRoadPath(myPath) // 해당하는 경로 설정
                        .build();

                // 생성된 Notification 객체 저장
                savedTimes.add(notificationRepository.save(notification));

                Map<String, Object> notificationData = new HashMap<>();
                notificationData.put("day", notification.getDayOfWeek());
                notificationData.put("from_time", notification.getFromTime().toString());
                notificationData.put("to_time", notification.getToTime().toString());

                notificationJsonList.add(notificationData);
            }

            String fieldName = myPath.getMember().getId().toString();

            String notificationJsonArray = convertListToJson(notificationJsonList);
            System.out.println(notificationJsonArray);

            // redis에 저장
            redisTemplate.opsForHash().put("user_notifications", fieldName, notificationJsonArray);

            // 알림 시간을 저장한 후 경로 알림 설정을 업데이트
            ResponseEntity<Response.Body> updateNotificationResult = myFindRoadService.updateRouteNotification(request.getMyPathId(), true);
            if (!updateNotificationResult.getStatusCode().is2xxSuccessful()) {
                return updateNotificationResult;
            }

            return response.success(savedTimes, "마이 길찾기 알람 저장 성공 및 알림 설정 변경 완료", HttpStatus.OK);


        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return response.fail("해당 요일에 대한 알림 설정이 이미 존재합니다", HttpStatus.BAD_GATEWAY);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    private String convertListToJson(List<Map<String, Object>> notificationJsonList) throws JsonProcessingException {
        return objectMapper.writeValueAsString(notificationJsonList);
    }

    public ResponseEntity<Response.Body> getNotificationTimes(Long myPathId) {
        try {
            List<Notification> notifications = notificationRepository.findByMyFindRoadPathId(myPathId);
            MyFindRoadNotificationResponse myFindRoadNotificationResponse = new MyFindRoadNotificationResponse();
            myFindRoadNotificationResponse.setMyFindRoadPathId(myPathId);
            MyFindRoadPath myFindRoadPath = myFindRoadPathRepository.findById(myPathId).orElseThrow(
                    () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
            );
            myFindRoadNotificationResponse.setEnabled(myFindRoadPath.getNotification());
            if (myFindRoadNotificationResponse.isEnabled()) {
                List<MyFindRoadNotificationResponse.NotificationTime> notificationTimes = new ArrayList<>();
                for (Notification notification : notifications) {
                    MyFindRoadNotificationResponse.NotificationTime notificationTime = MyFindRoadNotificationResponse.NotificationTime.builder()
                            .dayOfWeek(notification.getDayOfWeek())
                            .fromTime(notification.getFromTime())
                            .toTime(notification.getToTime())
                            .build();
                    notificationTimes.add(notificationTime);
                }
                myFindRoadNotificationResponse.setNotificationTimes(notificationTimes);
            }
            return response.success(myFindRoadNotificationResponse, "마이 길찾기 알람 찾기 성공", HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }

    @Transactional
    public ResponseEntity<Response.Body> deleteNotificationTimes(Long myPathId) {
        try {
            MyFindRoadPath myFindRoadPath = myFindRoadPathRepository.findById(myPathId).orElseThrow(
                    () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
            );
            notificationRepository.deleteByMyFindRoadPath(myFindRoadPath);

            String fieldName = myFindRoadPath.getMember().getId().toString();
            // redis에 데이터도 삭제
            redisTemplate.opsForHash().delete("user_notifications", fieldName);

            return response.success(null, "마이 길찾기 알람 삭제 성공", HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ResponseEntity<Response.Body> updateNotificationTimes(MyFindRoadNotificationRequest request) {
        try {
            // 요청으로 받은 경로에 대한 알림 찾기
            MyFindRoadPath myPath = myFindRoadPathRepository.findById(request.getMyPathId())
                    .orElseThrow(() -> new EntityNotFoundException("해당 경로가 존재하지 않습니다."));

            // 알림 시간 업데이트 또는 새 알림 저장
            List<Notification> notifications = notificationRepository.findByMyFindRoadPathId(myPath.getId());
            List<Notification> savedTimes = new ArrayList<>();
            List<Map<String, Object>> notificationJsonList = new ArrayList<>();

            // 요청으로 받은 알림 시간 리스트를 순회
            for (int i = 0; i < request.getDayTimeRanges().size(); i++) {
                MyFindRoadNotificationRequest.DayTimeRange timeRange = request.getDayTimeRanges().get(i);
                Notification notification;

                // 시간 검증 로직 추가
                LocalTime fromTime = LocalTime.parse(timeRange.getFromTime());
                LocalTime toTime = LocalTime.parse(timeRange.getToTime());
                if (fromTime.isAfter(toTime) || fromTime.equals(toTime)) {
                    throw new IllegalArgumentException("fromTime은 toTime보다 이전이어야 합니다: "
                            + "fromTime=" + fromTime + ", toTime=" + toTime);
                }

                // 기존 알림이 있다면 업데이트, 부족하면 새로 생성
                if (i < notifications.size()) {
                    notification = notifications.get(i);
                    notification.updateNotification(timeRange.getDay(), fromTime, toTime);
                } else {
                    notification = Notification.builder()
                            .dayOfWeek(timeRange.getDay())
                            .fromTime(fromTime)
                            .toTime(toTime)
                            .myFindRoadPath(myPath)
                            .build();
                }

                // 저장
                savedTimes.add(notificationRepository.save(notification));

                // Redis에 저장할 JSON 데이터 준비
                Map<String, Object> notificationData = new HashMap<>();
                notificationData.put("day", notification.getDayOfWeek());
                notificationData.put("from_time", notification.getFromTime().toString());
                notificationData.put("to_time", notification.getToTime().toString());
                notificationJsonList.add(notificationData);
            }

            // Redis의 기존 값을 삭제
            String fieldName = myPath.getMember().getId().toString();
            redisTemplate.opsForHash().delete("user_notifications", fieldName);

            // 새로운 알림 데이터를 Redis에 저장
            String notificationJsonArray = convertListToJson(notificationJsonList);
            redisTemplate.opsForHash().put("user_notifications", fieldName, notificationJsonArray);

            return response.success(savedTimes, "알림 시간이 성공적으로 업데이트 되었습니다.", HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return response.fail("해당 요일에 대한 알림 설정이 이미 존재합니다", HttpStatus.BAD_GATEWAY);
        } catch (Exception e) {
            return response.fail(e.getMessage(), HttpStatus.BAD_GATEWAY);
        }
    }

    @Override
    public ResponseEntity<Response.Body> getPathId(Long notificationId) {
        try {
            Notification notification = notificationRepository.findById(notificationId)
                    .orElseThrow(() -> new EntityNotFoundException("해당 알림이 존재하지 않습니다."));
            Long myPathId = notification.getMyFindRoadPath().getId();
            return response.success(myPathId, "알림 경로 ID 조회 성공", HttpStatus.OK);
        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        }
    }
}