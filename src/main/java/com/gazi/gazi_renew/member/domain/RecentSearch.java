package com.gazi.gazi_renew.member.domain;

import com.gazi.gazi_renew.common.service.port.ClockHolder;
import com.gazi.gazi_renew.member.domain.dto.RecentSearchCreate;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RecentSearch {
    private final Long id;
    private final String stationName;
    private final String stationLine;
    private final Long memberId;
    private final LocalDateTime modifiedAt;
    @Builder
    public RecentSearch(Long id, String stationName, String stationLine, Long memberId, LocalDateTime modifiedAt) {
        this.id = id;
        this.stationName = stationName;
        this.stationLine = stationLine;
        this.memberId = memberId;
        this.modifiedAt = modifiedAt;
    }

    public static RecentSearch from(RecentSearchCreate recentSearchCreate, Long memberId, ClockHolder clockHolder) {
        return RecentSearch.builder()
                .stationName(recentSearchCreate.getStationName())
                .stationLine(recentSearchCreate.getStationLine())
                .memberId(memberId)
                .modifiedAt(clockHolder.now())
                .build();
    }
}
