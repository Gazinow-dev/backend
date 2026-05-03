package com.gazi.gazi_renew.station.controller.response;

import com.gazi.gazi_renew.station.domain.Station;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class StationResponse {
    private final Long id;
    private final List<String> line;
    private final String name;
    private final int stationCode;
    private final double lat;
    private final double lng;
    private final Integer issueStationCode;

    @Builder
    public StationResponse(Long id, List<String> line, String name, int stationCode,
                           double lat, double lng, Integer issueStationCode) {
        this.id = id;
        this.line = line;
        this.name = name;
        this.stationCode = stationCode;
        this.lat = lat;
        this.lng = lng;
        this.issueStationCode = issueStationCode;
    }

    public static StationResponse from(Station representative, List<String> lines) {
        return StationResponse.builder()
                .id(representative.getId())
                .line(lines)
                .name(representative.getName())
                .stationCode(representative.getStationCode())
                .lat(representative.getLat())
                .lng(representative.getLng())
                .issueStationCode(representative.getIssueStationCode())
                .build();
    }
}
