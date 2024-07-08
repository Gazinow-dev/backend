package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.domain.MyFindRoadPath;
import com.gazi.gazi_renew.domain.Notification;
import com.gazi.gazi_renew.dto.MyFindRoadNotificationRequest;
import com.gazi.gazi_renew.dto.MyFindRoadNotificationResponse;
import com.gazi.gazi_renew.dto.Response;
import com.gazi.gazi_renew.repository.MyFindRoadPathRepository;
import com.gazi.gazi_renew.repository.NotificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class NotificationServiceImpl implements NotificationService{
    private final Response response;
    private final NotificationRepository notificationRepository;
    private final MyFindRoadPathRepository myFindRoadPathRepository;


    public List<Notification> getAll() {
        return notificationRepository.findAll();
    }

    public ResponseEntity<Response.Body> saveNotificationTimes(MyFindRoadNotificationRequest request) {
        try {
            List<Notification> savedTimes = new ArrayList<>();
            MyFindRoadPath myPath = myFindRoadPathRepository.findById(request.getMyPathId()).orElseThrow(
                    () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
            );

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
            }
            return response.success(savedTimes, "마이 길찾기 알람 저장 성공", HttpStatus.OK);


        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (DataIntegrityViolationException e) {
            return response.fail("해당 요일에 대한 알림 설정이 이미 존재합니다", HttpStatus.BAD_GATEWAY);
        } catch (Exception e){
            return response.fail(e.getMessage(),HttpStatus.BAD_GATEWAY);
        }
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
            return response.fail(e.getMessage(), HttpStatus.UNAUTHORIZED);
        }
    }

    @Transactional
    public ResponseEntity<Response.Body> deleteNotificationTimes(Long myPathId) {
        try {
            MyFindRoadPath myFindRoadPath = myFindRoadPathRepository.findById(myPathId).orElseThrow(
                    () -> new EntityNotFoundException("해당 경로가 존재하지 않습니다.")
            );
            notificationRepository.deleteByMyFindRoadPath(myFindRoadPath);
            return response.success(null, "마이 길찾기 알람 삭제 성공", HttpStatus.OK);

        } catch (EntityNotFoundException e) {
            return response.fail(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e){
            return response.fail(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
