package com.gazi.gazi_renew.member.domain.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class RecentSearchCreate {
    private final String stationName;
    private final String StationLine;
    @Builder
    public RecentSearchCreate(String stationName, String stationLine) {
        this.stationName = stationName;
        StationLine = stationLine;
    }
}
