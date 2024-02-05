package com.gazi.gazi_renew.service;

import com.gazi.gazi_renew.dto.IssueRequest;
import com.gazi.gazi_renew.dto.Response;
import org.springframework.http.ResponseEntity;

public interface IssueService {
    // 웹 크롤링

    // 이슈 저장
    public ResponseEntity<Response.Body> addIssue(IssueRequest dto);
    // 이슈 조회
    // 이슈 전체조회
    // 이슈 필터조회

}
