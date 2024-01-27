package com.gazi.gazi_renew.dto;

import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.RecentSearch;
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
