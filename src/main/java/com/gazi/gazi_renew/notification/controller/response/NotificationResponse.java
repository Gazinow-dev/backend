package com.gazi.gazi_renew.notification.controller.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
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

    // 다중 알림 시간 리스트를 처리하기 위한 내부 클래스
    @Getter
    public static class NotificationTime {
        private final String dayOfWeek;
        private final LocalTime fromTime;
        private final LocalTime toTime;
        @Builder
        public NotificationTime(String dayOfWeek, LocalTime fromTime, LocalTime toTime) {
            this.dayOfWeek = dayOfWeek;
            this.fromTime = fromTime;
            this.toTime = toTime;
        }
    }

}