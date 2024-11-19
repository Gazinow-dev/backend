package com.gazi.gazi_renew.route.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class MyFindRoadLaneCreate {
    private final String name; // 노선명
    private final int stationCode; //노선코드 ex:) 2
    @Builder
    public MyFindRoadLaneCreate(String name, int stationCode) {
        this.name = name;
        this.stationCode = stationCode;
    }
}