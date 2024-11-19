package com.gazi.gazi_renew.notification.controller.response;

import com.gazi.gazi_renew.notification.domain.Notification;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class NotificationResponse {
    private final boolean enabled;
    private final Long myFindRoadPathId; // 경로 ID
    private final List<NotificationTime> notificationTimes;
    @Builder
    public NotificationResponse(boolean enabled, Long myFindRoadPathId, List<NotificationTime> notificationTimes) {
        this.enabled = enabled;
        this.myFindRoadPathId = myFindRoadPathId;
        this.notificationTimes = notificationTimes;
    }
    // 알림 상태값 없데이트
    public NotificationResponse updateEnabled(boolean enabled) {
        return NotificationResponse.builder()
                .enabled(enabled)
                .myFindRoadPathId(this.myFindRoadPathId)
                .notificationTimes(this.notificationTimes)
                .build();

    }
    // 다중 알림 시간 리스트를 처리하기 위한 내부 클래스
    @Getter
    public static class NotificationTime {
        private final Long id;
        private final String dayOfWeek;
        private final LocalTime fromTime;
        private final LocalTime toTime;
        @Builder
        public NotificationTime(Long id, String dayOfWeek, LocalTime fromTime, LocalTime toTime) {
            this.id = id;
            this.dayOfWeek = dayOfWeek;
            this.fromTime = fromTime;
            this.toTime = toTime;
        }
    }

    public static NotificationResponse fromList(MyFindRoad myFindRoad, List<Notification> notificationList) {
        if (myFindRoad == null) {
            throw new IllegalArgumentException("해당 경로가 존재하지 않습니다.");
        }
        NotificationResponse notificationResponse = NotificationResponse.builder()
                .enabled(myFindRoad.getNotification())
                .myFindRoadPathId(myFindRoad.getId())
                .build();
        if (notificationResponse.isEnabled()) {
            List<NotificationResponse.NotificationTime> notificationTimes = new ArrayList<>();
            for (Notification notification : notificationList) {
                NotificationResponse.NotificationTime notificationTime = NotificationTime.builder()
                        .id(notification.getId())
                        .dayOfWeek(notification.getDayOfWeek())
                        .fromTime(notification.getFromTime())
                        .toTime(notification.getToTime())
                        .build();
                notificationTimes.add(notificationTime);

            }
            notificationResponse = NotificationResponse.builder()
                    .enabled(notificationResponse.enabled)
                    .myFindRoadPathId(notificationResponse.myFindRoadPathId)
                    .notificationTimes(notificationTimes)
                    .build();
        }
        return notificationResponse;
    }

}