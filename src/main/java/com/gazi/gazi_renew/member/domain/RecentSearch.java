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
    private final Member member;
    private final LocalDateTime modifiedAt;
    @Builder
    public RecentSearch(Long id, String stationName, String stationLine, Member member, LocalDateTime modifiedAt) {
        this.id = id;
        this.stationName = stationName;
        this.stationLine = stationLine;
        this.member = member;
        this.modifiedAt = modifiedAt;
    }

    public static RecentSearch from(RecentSearchCreate recentSearchCreate, Member member) {
        return RecentSearch.builder()
                .stationName(recentSearchCreate.getStationName())
                .stationLine(recentSearchCreate.getStationLine())
                .member(member)
                .build();
    }
    public RecentSearch updateModifiedAt(ClockHolder clockHolder) {
        return RecentSearch.builder()
                .id(this.id)
                .stationName(this.stationName)
                .stationLine(this.stationLine)
                .member(this.member)
                .modifiedAt(clockHolder.now())
                .build();
    }
}
