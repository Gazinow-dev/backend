package com.gazi.gazi_renew.route.domain;

import com.gazi.gazi_renew.member.domain.Member;
import com.gazi.gazi_renew.route.domain.dto.MyFindRoadCreate;
import com.gazi.gazi_renew.route.domain.dto.SubPath;
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
    private final List<SubPath> subPaths;
    private final Boolean notification;

    public static MyFindRoad from(MyFindRoadCreate myFindRoadCreate) {
        return MyFindRoad.builder()
                .roadName(myFindRoadCreate.getRoadName())
                .totalTime(myFindRoadCreate.getTotalTime())
                .stationTransitCount(myFindRoadCreate.getStationTransitCount())
                .firstStartStation(myFindRoadCreate.getFirstStartStation())
                .lastEndStation(myFindRoadCreate.getLastEndStation())
                .subPaths(myFindRoadCreate.getSubPaths())
                .build();
    }
    @Builder
    public MyFindRoad(Long id, String roadName, int totalTime, int stationTransitCount, String firstStartStation, String lastEndStation, Member member, List<SubPath> subPaths, Boolean notification) {
        this.id = id;
        this.roadName = roadName;
        this.totalTime = totalTime;
        this.stationTransitCount = stationTransitCount;
        this.firstStartStation = firstStartStation;
        this.lastEndStation = lastEndStation;
        this.member = member;
        this.subPaths = subPaths;
        this.notification = notification;
    }



}
