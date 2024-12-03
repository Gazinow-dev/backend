package com.gazi.gazi_renew.notification.domain;

import com.gazi.gazi_renew.common.exception.ErrorCode;
import com.gazi.gazi_renew.route.domain.MyFindRoad;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadNotificationCreate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
public class Notification {
    private final Long id;
    private final String dayOfWeek;
    private final LocalTime fromTime;
    private final LocalTime toTime;
    private final Long myFindRoadPathId;
    @Builder
    public Notification(Long id, String dayOfWeek, LocalTime fromTime, LocalTime toTime, Long myFindRoadPathId) {
        this.id = id;
        this.dayOfWeek = dayOfWeek;
        this.fromTime = fromTime;
        this.toTime = toTime;
        this.myFindRoadPathId = myFindRoadPathId;
    }

    public static List<Notification> from(MyFindRoadNotificationCreate myFindRoadNotificationCreate, Long myFindRoadPathId) {
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
                    .myFindRoadPathId(myFindRoadPathId)
                    .build();
            savedTimes.add(notification);
        }
        return savedTimes;

    }
    // 알림 시간 검증
    private static void validateTimeOrder(LocalTime fromTime, LocalTime toTime) {
        if (fromTime.isAfter(toTime) || fromTime.equals(toTime)) {
            throw ErrorCode.throwInvalidTimeRange();
        }

    }

    //초기 알림 저장
    public static List<Notification> initNotification(Long myFindRoadPathId) {
        List<Notification> savedTimes = new ArrayList<>();
        List<String> dayList = Arrays.asList("월", "화", "수", "목", "금");

        for (String day : dayList) {
            LocalTime fromTime = LocalTime.parse("07:00");
            LocalTime toTime = LocalTime.parse("09:00");

            Notification notification = Notification.builder()
                    .dayOfWeek(day)
                    .fromTime(fromTime)
                    .toTime(toTime)
                    .myFindRoadPathId(myFindRoadPathId)
                    .build();
            savedTimes.add(notification);
        }
        return savedTimes;
    }
}
