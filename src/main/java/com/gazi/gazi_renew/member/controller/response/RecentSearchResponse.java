package com.gazi.gazi_renew.member.controller.response;

import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;
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

    public static RecentSearchResponse getDto(RecentSearchEntity recentSearchEntity){
        return RecentSearchResponse.builder()
                .id(recentSearchEntity.getId())
                .stationName(recentSearchEntity.getStationName())
                .stationLine(recentSearchEntity.getStationLine())
                .build();
    }
}
