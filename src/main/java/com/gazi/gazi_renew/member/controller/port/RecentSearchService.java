package com.gazi.gazi_renew.member.controller.port;

import com.gazi.gazi_renew.member.domain.RecentSearch;
import com.gazi.gazi_renew.common.controller.response.Response;
import org.springframework.http.ResponseEntity;

public interface RecentSearchService {
    // 최근 검색 조회
    ResponseEntity<Response.Body> recentGet();
    // 최근 검색 추가
    ResponseEntity<Response.Body> recentAdd(RecentSearch dto);

    // 최근 검색 삭제
    ResponseEntity<Response.Body> recentDelete(Long recentSearchId);
}
