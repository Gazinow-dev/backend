package com.gazi.gazi_renew.member.controller.port;

import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.member.domain.dto.RecentSearchCreate;

import java.util.List;

public interface RecentSearchService {
    // 최근 검색 조회
    List<RecentSearch> getRecentSearch();
    // 최근 검색 추가
    RecentSearch addRecentSearch(RecentSearchCreate recentSearchCreate);

    // 최근 검색 삭제
    void recentDelete(Long recentSearchId);
}
