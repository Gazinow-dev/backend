package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadSubPath;
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
    private final Member member;
    private final List<MyFindRoadSubPath> myFindRoadSubPaths;
    private final Boolean notification;

    public static MyFindRoad from(MyFindRoadCreate myFindRoadCreate, Member member) {
        return MyFindRoad.builder()
                .roadName(myFindRoadCreate.getRoadName())
                .totalTime(myFindRoadCreate.getTotalTime())
                .stationTransitCount(myFindRoadCreate.getStationTransitCount())
                .firstStartStation(myFindRoadCreate.getFirstStartStation())
                .lastEndStation(myFindRoadCreate.getLastEndStation())
                .member(member)
                .myFindRoadSubPaths(myFindRoadCreate.getMyFindRoadSubPaths())
                .notification(false)
                .build();
    }
    @Builder
    public MyFindRoad(Long id, String roadName, int totalTime, int stationTransitCount, String firstStartStation, String lastEndStation, Member member, List<MyFindRoadSubPath> myFindRoadSubPaths, Boolean notification) {
        this.id = id;
        this.roadName = roadName;
        this.totalTime = totalTime;
        this.stationTransitCount = stationTransitCount;
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.member = member;
        this.myFindRoadSubPaths = myFindRoadSubPaths;
        this.notification = notification;
    }
    public MyFindRoad updateNotification(Boolean enabled) {
        return MyFindRoad.builder()
                .roadName(this.getRoadName())
                .totalTime(this.getTotalTime())
                .stationTransitCount(this.getStationTransitCount())
                .firstStartStation(this.getFirstStartStation())
                .lastEndStation(this.getLastEndStation())
                .member(this.member)
                .myFindRoadSubPaths(this.getMyFindRoadSubPaths())
                .notification(enabled)
                .build();
    }
}
