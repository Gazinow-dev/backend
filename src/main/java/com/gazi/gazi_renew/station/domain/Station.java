package com.gazi.gazi_renew.station.domain;

import com.gazi.gazi_renew.issue.domain.Issue;
import jakarta.persistence.EntityNotFoundException;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class Station {
    private final Long id;
    private final String line;
    private final String name;
    private final int stationCode;
    private final double lat;
    private final double lng;
    private final Integer issueStationCode;
    @Builder
    public Station(Long id, String line, String name, int stationCode, double lat, double lng, Integer issueStationCode) {
        this.id = id;
        this.line = line;
        this.name = name;
        this.stationCode = stationCode;
        this.lat = lat;
        this.lng = lng;
        this.issueStationCode = issueStationCode;
    }

    public static Station toFirstStation(String name, List<Station> stationList) {
        if (stationList == null || stationList.isEmpty()) {
            throw new EntityNotFoundException("Station이 존재하지 않습니다");
        }
        Station firstStation = stationList.get(0);
        int k = 0;
        // 필터링
        if (!stationList.isEmpty() && stationList.size() >= 2) {
            for (Station station : stationList) {
                int stationLength = station.getName().length(); // 찾은 entity 역글자수
                int result = stationLength - name.length();

                if (k > result) {
                    firstStation = station;
                    k = result;
                }
            }
        }
        return firstStation;
    }
}















