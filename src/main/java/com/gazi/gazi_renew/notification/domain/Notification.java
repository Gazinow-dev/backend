package com.gazi.gazi_renew.notification.domain;

import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
public class Notification {
    private final Long id;
    private final String dayOfWeek;
    private final LocalTime fromTime;
    private final LocalTime toTime;
    private final MyFindRoad myFindRoad;
    @Builder
    public Notification(Long id, String dayOfWeek, LocalTime fromTime, LocalTime toTime, MyFindRoad myFindRoad) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.myFindRoad = myFindRoad;
    }

    public static List<Notification> from(MyFindRoadNotificationCreate myFindRoadNotificationCreate, MyFindRoad myFindRoad) {
        List<Notification> savedTimes = new ArrayList<>();
        for (MyFindRoadNotificationCreate.DayTimeRange dayTimeRange : myFindRoadNotificationCreate.getDayTimeRanges()) {
            String day = dayTimeRange.getDay();
            LocalTime fromTime = LocalTime.parse(dayTimeRange.getFromTime());
            LocalTime toTime = LocalTime.parse(dayTimeRange.getToTime());
            // 시간 검증 로직
            validateTimeOrder(fromTime, toTime);
            Notification notification = Notification.builder()
                    .dayOfWeek(day)
                    .fromTime(fromTime)
                    .toTime(toTime)
                    .myFindRoad(myFindRoad)
                    .build();
            savedTimes.add(notification);
        }
        return savedTimes;

    }
    // 알림 시간 검증
    private static void validateTimeOrder(LocalTime fromTime, LocalTime toTime) {
        if (fromTime.isAfter(toTime) || fromTime.equals(toTime)) {
            throw new IllegalArgumentException("fromTime은 toTime보다 이전이어야 합니다: "
                    + "fromTime=" + fromTime + ", toTime=" + toTime);
        }
    }
}
