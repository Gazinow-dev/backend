package com.gazi.gazi_renew.station.controller.response;


import com.gazi.gazi_renew.station.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SubwayInfoResponse {
    private final String name;
    private final String line;
    @Builder
    public SubwayInfoResponse(String name, String line) {
        this.name = name;
        this.line = line;
    }

    public static List<SubwayInfoResponse> fromList(List<Station> stationList) {
        return stationList.stream()
                .map(station -> SubwayInfoResponse.builder()
                        .line(station.getLine())
                        .name(station.getName())
                        .build()
                ).collect(Collectors.toList());
    }
}