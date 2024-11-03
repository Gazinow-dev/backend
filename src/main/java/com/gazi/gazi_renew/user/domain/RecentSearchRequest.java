package com.gazi.gazi_renew.user.domain;

import com.gazi.gazi_renew.user.infrastructure.MemberEntity;
import com.gazi.gazi_renew.user.infrastructure.RecentSearchEntity;
import lombok.Getter;

@Getter
public class RecentSearchRequest {
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
