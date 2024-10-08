package com.gazi.gazi_renew.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
public class MyFindRoadNotificationResponse {
    private boolean enabled;
    private Long myFindRoadPathId; // 경로 ID
    private List<NotificationTime> notificationTimes;
    public MyFindRoadNotificationResponse(boolean enabled, Long myFindRoadPathId, List<NotificationTime> notificationTimes) {
        this.enabled = enabled;
        this.myFindRoadPathId = myFindRoadPathId;
        this.notificationTimes = notificationTimes;
    }

    // 다중 알림 시간 리스트를 처리하기 위한 내부 클래스
    @Getter
    @Setter
    @Builder
    @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
    public static class NotificationTime {
        private String dayOfWeek;
        private LocalTime fromTime;
        private LocalTime toTime;
    }

}