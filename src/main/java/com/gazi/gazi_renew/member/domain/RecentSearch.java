package com.gazi.gazi_renew.member.domain;

import com.gazi.gazi_renew.member.infrastructure.MemberEntity;
import com.gazi.gazi_renew.member.infrastructure.RecentSearchEntity;
import lombok.Getter;

@Getter
public class RecentSearch {
    String stationName;
    String stationLine;

    public RecentSearchEntity toRecentSearch(MemberEntity memberEntity){
        return RecentSearchEntity.builder()
                .stationName(stationName)
                .stationLine(stationLine)
                .memberEntity(memberEntity)
                .build();
    }
}
