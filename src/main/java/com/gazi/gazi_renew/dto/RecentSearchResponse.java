package com.gazi.gazi_renew.dto;

import com.gazi.gazi_renew.domain.RecentSearch;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RecentSearchResponse {

    private Long id;
    private String stationName;
    private String stationLine;

    public static RecentSearchResponse getDto(RecentSearch recentSearch){
        return RecentSearchResponse.builder()
                .id(recentSearch.getId())
                .stationName(recentSearch.getStationName())
                .stationLine(recentSearch.getStationLine())
                .build();
    }
}
