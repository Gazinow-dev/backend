package com.gazi.gazi_renew.route.domain.dto;

import lombok.Builder;
import lombok.Getter;


@Getter
public class MyFindRoadStationCreate {
    private final int index; // 정류장 순번
    private final String stationName;
    private final int stationCode;
    @Builder
    public MyFindRoadStationCreate(int index, String stationName, int stationCode) {
        this.index = index;
        this.stationName = stationName;
        this.stationCode = stationCode;
    }
}