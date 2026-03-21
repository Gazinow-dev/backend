package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class MyFindRoad {
    private final Long id;
    private final String roadName;
    private final int totalTime; // 총소요시간
    private final int stationTransitCount;
    private final String firstStartStation;
    private final String lastEndStation;
    private final Integer walkingTimeFromStartStation; // 출발역까지 도보 시간 (분)
    private final Integer walkingTimeToEndStation;     // 도착역에서 도보 시간 (분)
    private final Long memberId;
    private final Boolean memberNextDayIssueNotificationEnabled;
    private final List<MyFindRoadSubPath> subPaths;
    private final Boolean notification;

    public static MyFindRoad from(MyFindRoadCreate myFindRoadCreate, Long memberId) {
        return MyFindRoad.builder()
                .roadName(myFindRoadCreate.getRoadName())
                .totalTime(myFindRoadCreate.getTotalTime())
                .stationTransitCount(myFindRoadCreate.getStationTransitCount())
                .firstStartStation(myFindRoadCreate.getFirstStartStation())
                .lastEndStation(myFindRoadCreate.getLastEndStation())
                .walkingTimeFromStartStation(
                        myFindRoadCreate.getWalkingTimeFromStartStation() != null
                                ? myFindRoadCreate.getWalkingTimeFromStartStation()
                                : 5)
                .walkingTimeToEndStation(myFindRoadCreate.getWalkingTimeToEndStation())
                .memberId(memberId)
                .notification(true)
                .build();
    }
    @Builder
    public MyFindRoad(Long id, String roadName, int totalTime, int stationTransitCount, String firstStartStation, String lastEndStation, Integer walkingTimeFromStartStation, Integer walkingTimeToEndStation, Long memberId, Boolean memberNextDayIssueNotificationEnabled, List<MyFindRoadSubPath> subPaths, Boolean notification) {
        this.id = id;
        this.roadName = roadName;
        this.totalTime = totalTime;
        this.stationTransitCount = stationTransitCount;
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.walkingTimeFromStartStation = walkingTimeFromStartStation;
        this.walkingTimeToEndStation = walkingTimeToEndStation;
        this.memberId = memberId;
        this.memberNextDayIssueNotificationEnabled = memberNextDayIssueNotificationEnabled;
        this.subPaths = subPaths;
        this.notification = notification;
    }
    public MyFindRoad updateNotification(Boolean enabled) {
        return MyFindRoad.builder()
                .id(this.id)
                .roadName(this.getRoadName())
                .totalTime(this.getTotalTime())
                .stationTransitCount(this.getStationTransitCount())
                .firstStartStation(this.getFirstStartStation())
                .lastEndStation(this.getLastEndStation())
                .walkingTimeFromStartStation(this.getWalkingTimeFromStartStation())
                .walkingTimeToEndStation(this.getWalkingTimeToEndStation())
                .memberId(this.memberId)
                .memberNextDayIssueNotificationEnabled(this.getMemberNextDayIssueNotificationEnabled())
                .subPaths(this.getSubPaths())
                .notification(enabled)
                .build();
    }
    public MyFindRoad updateMemberNextDayIssueNotification(Boolean enabled) {
        return MyFindRoad.builder()
                .id(this.id)
                .roadName(this.getRoadName())
                .totalTime(this.getTotalTime())
                .stationTransitCount(this.getStationTransitCount())
                .firstStartStation(this.getFirstStartStation())
                .lastEndStation(this.getLastEndStation())
                .walkingTimeFromStartStation(this.getWalkingTimeFromStartStation())
                .walkingTimeToEndStation(this.getWalkingTimeToEndStation())
                .memberId(this.memberId)
                .memberNextDayIssueNotificationEnabled(enabled)
                .subPaths(this.getSubPaths())
                .notification(this.notification)
                .build();
    }

    public MyFindRoad updateSubPaths(List<MyFindRoadSubPath> updatedSubPaths) {
        return MyFindRoad.builder()
                .id(this.id)
                .roadName(this.roadName)
                .totalTime(this.totalTime)
                .stationTransitCount(this.stationTransitCount)
                .firstStartStation(this.firstStartStation)
                .lastEndStation(this.lastEndStation)
                .walkingTimeFromStartStation(this.walkingTimeFromStartStation)
                .walkingTimeToEndStation(this.walkingTimeToEndStation)
                .memberId(this.memberId)
                .memberNextDayIssueNotificationEnabled(this.memberNextDayIssueNotificationEnabled)
                .subPaths(updatedSubPaths)
                .notification(this.notification)
                .build();
    }
}
