package com.gazi.gazi_renew.route.domain;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyFindRoadNotification {
    private final Long myPathId;
    private final List<DayTimeRange> dayTimeRanges; // 요일과 시간 범위 리스트

    @Builder
    public MyFindRoadNotification(Long myPathId, List<DayTimeRange> dayTimeRanges) {
        this.myPathId = myPathId;
        this.dayTimeRanges = dayTimeRanges;
    }

    @Getter
    public static class DayTimeRange {
        private final String day;
        private final String fromTime;
        private final String toTime;
        @Builder
        public DayTimeRange(String day, String fromTime, String toTime) {
            this.day = day;
            this.fromTime = fromTime;
            this.toTime = toTime;
        }
    }
}
