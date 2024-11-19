package com.gazi.gazi_renew.member.controller.response;

import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class RecentSearchResponse {
    private final Long id;
    private final String stationName;
    private final String stationLine;
    @Builder
    public RecentSearchResponse(Long id, String stationName, String stationLine) {
        this.id = id;
        this.stationName = stationName;
        this.stationLine = stationLine;
    }
    public static RecentSearchResponse from(RecentSearch recentSearch){
        return RecentSearchResponse.builder()
                .id(recentSearch.getId())
                .stationName(recentSearch.getStationName())
                .stationLine(recentSearch.getStationLine())
                .build();
    }
    public static List<RecentSearchResponse> fromList(List<RecentSearch> recentSearchList) {
        return recentSearchList.stream().map(RecentSearchResponse::from)
                .collect(Collectors.toList());
    }
}
