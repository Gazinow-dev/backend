package com.gazi.gazi_renew.dto;

import com.gazi.gazi_renew.domain.Member;
import com.gazi.gazi_renew.domain.RecentSearch;
import lombok.Getter;

@Getter
public class RecentSearchRequest {
    String stationName;
    String stationLine;
    int stationCode;

    public RecentSearch toRecentSearch(Member member){
        return RecentSearch.builder()
                .stationName(stationName)
                .stationLine(stationLine)
                .stationCode(stationCode)
                .member(member)
                .build();
    }
}
