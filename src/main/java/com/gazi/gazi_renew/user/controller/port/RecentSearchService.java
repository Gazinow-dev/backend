package com.gazi.gazi_renew.user.controller.port;

import com.gazi.gazi_renew.user.domain.RecentSearchRequest;
import com.gazi.gazi_renew.common.controller.response.Response;
import org.springframework.http.ResponseEntity;

public interface RecentSearchService {
    // 최근 검색 조회
    ResponseEntity<Response.Body> recentGet();
    // 최근 검색 추가
    ResponseEntity<Response.Body> recentAdd(RecentSearchRequest dto);

    // 최근 검색 삭제
    ResponseEntity<Response.Body> recentDelete(Long recentSearchId);
}
