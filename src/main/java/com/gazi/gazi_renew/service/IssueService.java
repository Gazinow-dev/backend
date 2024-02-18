package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.dto.Response;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

public interface IssueService {
    // 웹 크롤링

    // 이슈 저장
    ResponseEntity<Response.Body> addIssue(IssueRequest dto);
    // 이슈 조회
    ResponseEntity<Response.Body> getIssue(Long id);
    // 이슈 전체조회
    ResponseEntity<Response.Body> getIssues(Pageable pageable);
    // 이슈 필터조회
    ResponseEntity<Response.Body> getLineByIssues(String line,Pageable pageable);

    //인기 이슈 조회
    ResponseEntity<Response.Body> getPopularIssues();
}
