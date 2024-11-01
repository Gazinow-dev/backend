package com.gazi.gazi_renew.user.domain;

import com.gazi.gazi_renew.user.infrastructure.Member;
import com.gazi.gazi_renew.user.infrastructure.RecentSearch;
import lombok.Getter;

@Getter
public class RecentSearchRequest {
    String stationName;
    String stationLine;

    public RecentSearch toRecentSearch(Member member){
        return RecentSearch.builder()
                .stationName(stationName)
                .stationLine(stationLine)
                .member(member)
                .build();
    }
}
