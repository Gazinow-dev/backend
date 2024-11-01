package com.gazi.gazi_renew.route.domain;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class MyFindRoadNotificationRequest {
    Long myPathId;

    @Setter
    @Getter
    private List<DayTimeRange> dayTimeRanges; // 요일과 시간 범위 리스트

    // 내부 클래스 정의: 요일과 해당 시간 범위를 나타내는 클래스
    @Setter
    @Getter
    public static class DayTimeRange {
        // Getters and Setters
        private String day;
        private String fromTime;
        private String toTime;

    }
}
