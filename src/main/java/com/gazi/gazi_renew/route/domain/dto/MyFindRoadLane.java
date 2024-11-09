package com.gazi.gazi_renew.route.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MyFindRoadLane {
    private final String name; // 노선명
    private final int stationCode; //노선코드 ex:) 2
    private final String startName; //승차 정류장
    private final String endName; // 하차 정류장
    @Builder
    public MyFindRoadLane(String name, int stationCode, String startName, String endName) {
        this.name = name;
        this.stationCode = stationCode;
        this.startName = startName;
        this.endName = endName;
    }
}