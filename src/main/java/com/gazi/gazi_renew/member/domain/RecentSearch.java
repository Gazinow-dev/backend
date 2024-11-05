package com.gazi.gazi_renew.member.domain;

import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;
import lombok.Builder;
import lombok.Getter;

@Getter
public class RecentSearch {
    private final String stationName;
    private final String stationLine;
    @Builder
    public RecentSearch(String stationName, String stationLine) {
        this.stationName = stationName;
        this.stationLine = stationLine;
    }


    public RecentSearchEntity toRecentSearch(MemberEntity memberEntity){
        return RecentSearchEntity.builder()
                .stationName(stationName)
                .stationLine(stationLine)
                .memberEntity(memberEntity)
                .build();
    }
}
